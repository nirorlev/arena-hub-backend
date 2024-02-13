//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.oss.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import com.threeatom.common.config.OssConfigData;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.oss.AliyunOssService;
import com.threeatom.guidecore.util.I18NUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliyunOssServiceImpl implements AliyunOssService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunOssServiceImpl.class);
    public static final String TAG = "aliyun";
    private OssConfigData ossConfig;
    private OSS ossClient;

    public AliyunOssServiceImpl(OssConfigData ossConfig) {
        this.ossConfig = ossConfig;
    }

    private OSS getOssClient() {
        if (this.ossClient != null) {
            return this.ossClient;
        } else {
            if (this.isEnable()) {
                this.ossClient =
                        (new OSSClientBuilder())
                                .build(
                                        this.ossConfig.getEndpoint(),
                                        this.ossConfig.getAccessKeyId(),
                                        this.ossConfig.getAccessKeySecret());
            }

            return this.ossClient;
        }
    }

    public String getProviderName() {
        return "aliyun";
    }

    public boolean isEnable() {
        return this.ossConfig != null;
    }

    public PutObjectResult uploadObject(String bucketName, String objectName, InputStream is) {
        OSS oss = this.getOssClient();
        PutObjectResult result = oss.putObject(bucketName, objectName, is);
        LOGGER.info(result.toString());
        return result;
    }

    public String getCurrentBucketName() {
        return this.ossConfig.getBucketName();
    }

    @Override
    public String getBusinessName() {
        return this.ossConfig.getBusinessName();
    }

    @Override
    public String getEndPoint() {
        return this.ossConfig.getEndpoint();
    }

    @Override
    public String getAccessKeyId() {
        return this.ossConfig.getAccessKeyId();
    }

    @Override
    public String getAccessKeySecret() {
        return this.ossConfig.getAccessKeySecret();
    }

    @Override
    public String getCallBackUrl() {
        return this.ossConfig.getCallbackUrl();
    }

    @Override
    public Long getMaxSize() {
        return this.ossConfig.getMaxSize();
    }

    public String getObjectUrl(String bucketName, String objectName, Integer sec) {
        OSS oss = this.getOssClient();
        Date expiration = new Date((new Date()).getTime() + (long) (sec * 1000));
        URL url = oss.generatePresignedUrl(bucketName, objectName, expiration);
        String urlStr = url.toString();
        if (urlStr.startsWith("http://")) {
            urlStr = urlStr.replaceFirst("http://", "https://");
        }

        return urlStr;
    }

    public JSONObject uploadObjectPolicy(
            String bucketName, String objectName, Map<String, Object> extMap) {
        OSS oss = this.getOssClient();
        String host = "https://" + bucketName + "." + this.ossConfig.getEndpoint();
        String callbackUrl = this.callbackUrl();

        try {
            long expireTime = 3000L;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000L;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConditions = new PolicyConditions();

            long maxSize = 734003200L;
            policyConditions.addConditionItem("content-length-range", 0L, maxSize);
            LOGGER.info("最大文件限制: " + maxSize + "mb: " + maxSize / 1024 / 1024);

            String postPolicy = oss.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = oss.calculatePostSignature(postPolicy);
            Map<String, Object> respMap = new LinkedHashMap();
            respMap.put("accessid", this.ossConfig.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000L));
            respMap.put("key", objectName);
            JSONObject jsonCallback = new JSONObject();
            StringBuffer callbackBodyBuffer = new StringBuffer();
            //            JSONObject callbackBodyJson = new JSONObject();

            callbackBodyBuffer
                    .append("{")
                    .append(
                            "\"mimeType\":${mimeType},\"size\":${size},\"object\":${object},\"etag\":${etag}");
            Iterator var20 = extMap.entrySet().iterator();

            while (var20.hasNext()) {
                Entry<String, Object> entry = (Entry) var20.next();
                callbackBodyBuffer
                        .append(",")
                        .append("\"")
                        .append((String) entry.getKey())
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(entry.getValue())
                        .append("\"");
                //                callbackBodyJson.put((String)entry.getKey(), entry.getValue());

            }

            callbackBodyBuffer.append("}");
            // 仅限小程序，因为小程序不支持分片
            if (extMap.get("ifFragment") != null && extMap.get("ifFragment").equals("no")) {
                jsonCallback.put("callbackUrl", callbackUrl);
                jsonCallback.put("callbackBody", callbackBodyBuffer.toString());
                jsonCallback.put("callbackBodyType", "application/json");
                String base64CallbackBody = BinaryUtil.toBase64String(jsonCallback.toString().getBytes());
                respMap.put("callback", base64CallbackBody);
                //                return JSONObject.toJSONString(respMap);
                return new JSONObject(respMap);
            }

            jsonCallback.put("url", callbackUrl);
            jsonCallback.put("body", callbackBodyBuffer);
            jsonCallback.put("contentType", "application/json");

            respMap.put("callback", jsonCallback);

            JSONObject json = new JSONObject(respMap);

            return json;
        } catch (Exception var22) {
            throw new SystemException(I18NUtil.get("signature.error"), var22);
        }
    }

    public String callbackUrl() {
        return this.ossConfig.getCallbackUrl();
    }

    public String getVideoSnapshot(String bucketName, String objectName, int frame) {
        OSS oss = this.getOssClient();
        String style = "video/snapshot,t_" + frame + ",f_jpg,w_0,h_0,m_fast";
        Date expiration = new Date((new Date()).getTime() + 600000L);
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);
        URL signedUrl = oss.generatePresignedUrl(req);
        String urlStr = signedUrl.toString();
        if (urlStr.startsWith("http://")) {
            urlStr = urlStr.replaceFirst("http://", "https://");
        }
        return urlStr;
    }

    public String uploadNetObject(String bucketName, String objectName, String url) {
        OSS oss = this.getOssClient();
        InputStream inputStream = null;

        try {
            inputStream = (new URL(url)).openStream();
        } catch (MalformedURLException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        if (inputStream == null) {
            return "";
        } else {
            PutObjectResult result = oss.putObject(bucketName, objectName, inputStream);
            LOGGER.info(result.toString());
            return objectName;
        }
    }

    // 删除单个文件：https://help.aliyun.com/document_detail/84842.htm?spm=a2c4g.11186623.2.7.4b787ff0z2Te8F#concept-84842-zh
    public void deleteObject(String bucketName, String objectName) {
        OSS oss = this.getOssClient();
        oss.deleteObject(bucketName, objectName);
    }
}
