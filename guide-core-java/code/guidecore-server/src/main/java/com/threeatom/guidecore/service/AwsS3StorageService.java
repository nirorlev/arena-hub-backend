package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;

public interface AwsS3StorageService {
    String generateSignedUrl(String key) throws SystemException;

    byte[] downloadFileFromS3(String bucketName, String key) throws SystemException;

    String uploadFileToS3(String fileUrl, Integer userId, Integer masterId) throws SystemException;
}
