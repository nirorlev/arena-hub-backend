package com.threeatom.guidecore.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.AwsUploadSignUrlConfiguration;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.utils.FileUtil;
import com.threeatom.utils.RandomUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3StorageServiceImpl implements AwsS3StorageService {

    private final RedisOperator redisOperator;
    private final AwsUploadSignUrlConfiguration awsUploadSignUrlConfiguration;


    private byte[] getAwsPrivateKey() throws SystemException {
        if (redisOperator.get("awsPrivateKey") != null) {
            return Base64.getDecoder().decode((String) redisOperator.get("awsPrivateKey"));
        }
        String keyName = awsUploadSignUrlConfiguration.getPrivateKeyFilePath();
        ClassPathResource classPathResource = new ClassPathResource("privatekey/" + keyName);
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(classPathResource.getPath());
        if (url == null) {
            String errMessage = "Cannot find AWS private key at path: " + "privatekey/" + keyName;
            log.error(errMessage);
            throw new SystemException(errMessage);
        }
        try (InputStream in = new FileInputStream(url.getFile());) {
            byte[] data = FileUtil.toByteArray(in);
            in.close();
            String byteToString = Base64.getEncoder().encodeToString(data);
            redisOperator.set("awsPrivateKey", byteToString);
            return data;
        } catch (IOException e) {
            String errMessage = "Failed to retrieve AWS private key";
            log.error(errMessage, e);
            throw new  SystemException(errMessage);
        }
    }

    private String buildFileS3Key(String fileUrl, Integer masterId, Integer userId) throws SystemException {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            String[] pathParts = path.split("/");
            String fileName = pathParts[pathParts.length - 1];
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = now.format(formatter);
            return String.format("%s/user/%s/%s/%s_%d_%s_%s",
                    masterId,
                    userId,
                    formattedDate,
                    userId,
                    System.currentTimeMillis(),
                    RandomUtils.getUUID(10),
                    fileName);
        } catch (MalformedURLException e) {
            String errMessage = "Failed to build S3 key from file URL";
            log.error(errMessage, e);
            throw new  SystemException(errMessage);
        }
    }

    private byte[] retrieveFileFromUrl(String fileUrl) throws SystemException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(fileUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    String errMessage = "Failed to download image. Null entity found.";
                    log.error(errMessage);
                    throw new  SystemException(errMessage);
                }
                try (InputStream inputStream = entity.getContent()){
                    return IOUtils.toByteArray(inputStream);
                }
            }
        } catch (IOException e) {
            String errMessage = "Failed to retrieve file from URL";
            log.error(errMessage, e);
            throw new  SystemException(errMessage);
        }
    }

    private void uploadFileToSignedUrl(byte[] filedata, String s3Url) throws SystemException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(s3Url);
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(filedata);
            httpPut.setEntity(byteArrayEntity);
            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                EntityUtils.consume(response.getEntity());
            }
        } catch (IOException e) {
            String errMessage = "Failed to upload file to S3 signed URL";
            log.error(errMessage, e);
            throw new  SystemException(errMessage);
        }
    }

    @Override
    public String generateSignedUrl(String key) throws SystemException {
        byte[] privateKey = getAwsPrivateKey();
        String param_UrlToBeSigned =
                "https://" + awsUploadSignUrlConfiguration.getDistributionDomain() + "/" + key;
        try {
            Date param_DateLessThan = ServiceUtils.parseIso8601Date("2123-07-15T22:20:00.000Z");
            String policy = CloudFrontService.buildPolicyForSignedUrl(
                param_UrlToBeSigned,
                param_DateLessThan,
                awsUploadSignUrlConfiguration.getLimitToIpAddressCIDR(),
                null);
            return CloudFrontService.signUrl(
                param_UrlToBeSigned,
                awsUploadSignUrlConfiguration.getKeyPairId(),
                privateKey,
                policy
            );
        } catch (ParseException | CloudFrontServiceException e) {
            String errMessage = "Failed to sign AWS S3 URL";
            log.error(errMessage, e);
            throw new  SystemException(errMessage);
        }
    }

    @Override
    public String uploadFileToS3(String fileUrl, Integer masterId, Integer userId) throws SystemException {
        byte[] fileData = retrieveFileFromUrl(fileUrl);
        String key = buildFileS3Key(fileUrl, masterId, userId);
        String signedUrl = generateSignedUrl(key);
        uploadFileToSignedUrl(fileData, signedUrl);
        return key;
    }
}
