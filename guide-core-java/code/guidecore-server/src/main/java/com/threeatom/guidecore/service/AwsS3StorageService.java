package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;

public interface AwsS3StorageService {
    String generateSignedUrl(String key) throws SystemException;
    String uploadFileToS3(String fileUrl, Integer masterId, Integer userId) throws SystemException;
}
