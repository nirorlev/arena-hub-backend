package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;

public interface AwsS3StorageService {
    byte[] retrieveFileFromS3(String key);

    String generateSignedUrl(String key) throws SystemException;

    String uploadFileToS3(String fileUrl, Integer userId, Integer masterId) throws SystemException;
}
