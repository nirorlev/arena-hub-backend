package com.threeatom.guidecore.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.config.AwsUploadSignUrlConfiguration;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.utils.FileUtil;
import com.threeatom.utils.RandomUtils;


/**
 * @author cvmcosta
 * @title: AwsS3StorageServiceImpl
 * @description: Responsible for handling file storage in s3
 */
@Service
public class AwsS3StorageServiceImpl implements AwsS3StorageService {

    @Autowired private RedisOperator redisOperator;

    @Autowired private AwsUploadSignUrlConfiguration awsUploadSignUrlConfiguration;


    private byte[] getAwsPrivateKey() throws SystemException {
        if (redisOperator.get("awsPrivateKey") != null) {
            return Base64.getDecoder().decode((String) redisOperator.get("awsPrivateKey"));
        }
        String keyName = awsUploadSignUrlConfiguration.getPrivateKeyFilePath();
        ClassPathResource classPathResource = new ClassPathResource("privatekey/" + keyName);
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(classPathResource.getPath());
        if (url == null) {
            throw new SystemException("Cannot find url: " + "privatekey/" + keyName);
        }
        try {
            InputStream in = new FileInputStream(url.getFile());
            byte[] data = FileUtil.toByteArray(in);
            in.close();
            String byteToString = Base64.getEncoder().encodeToString(data);
            redisOperator.set("awsPrivateKey", byteToString);
            return data;
        } catch (IOException e) {
            throw new  SystemException("Failed to retrieve AWS private key");
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
            throw new SystemException("Failed to build S3 key from file URL");
        }
    }

    private void retrieveFileAndUploadToS3(String fileUrl, String presignedUrl) throws SystemException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            HttpPut putRequest = new HttpPut(presignedUrl);
            putRequest.setHeader("Content-Type", connection.getContentType());
            InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, -1, null);
            putRequest.setEntity(inputStreamEntity);
            try (CloseableHttpResponse response = httpClient.execute(putRequest)) {
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new SystemException("Failed to upload file to S3");
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
            throw new SystemException("Error signing AWS URL");
        }
    }

    @Override
    public String uploadFileToS3(String fileUrl, Integer masterId, Integer userId) throws SystemException {
        String key = buildFileS3Key(fileUrl, masterId, userId);
        String signedUrl = generateSignedUrl(key);
        retrieveFileAndUploadToS3(fileUrl, signedUrl);
        return key;
    }
}
