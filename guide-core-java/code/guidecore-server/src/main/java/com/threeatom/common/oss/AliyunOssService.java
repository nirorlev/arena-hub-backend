package com.threeatom.common.oss;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.PutObjectResult;
import java.io.InputStream;
import java.util.Map;

public interface AliyunOssService {
    String getProviderName();

    boolean isEnable();

    PutObjectResult uploadObject(String bucketName, String objectName, InputStream is);

    String getCurrentBucketName();

    String getBusinessName();

    String getEndPoint();

    String getAccessKeyId();

    String getAccessKeySecret();

    String getCallBackUrl();

    Long getMaxSize();

    String getObjectUrl(String bucketName, String objectName, Integer sec);

    JSONObject uploadObjectPolicy(String bucketName, String objectName, Map<String, Object> extMap);

    String callbackUrl();

    String getVideoSnapshot(String bucketName, String objectName, int frame);

    String uploadNetObject(String bucketName, String objectName, String url);

    void deleteObject(String bucketName, String objectName);
}
