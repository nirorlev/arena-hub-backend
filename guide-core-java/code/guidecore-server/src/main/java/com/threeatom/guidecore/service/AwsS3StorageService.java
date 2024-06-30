package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;

/**
 * @author cvmcosta
 * @title: AwsS3StorageService
 * @description: Responsible for handling file storage in s3
 */
public interface AwsS3StorageService {
    public String generateSignedUrl(String key) throws SystemException;
    public String uploadFileToS3(String fileUrl, Integer masterId, Integer userId) throws SystemException;
}
