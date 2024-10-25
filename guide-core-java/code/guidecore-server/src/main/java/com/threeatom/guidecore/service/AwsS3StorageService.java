package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PortalUser;

public interface AwsS3StorageService {
    String generateSignedUrl(String key) throws SystemException;
    String uploadFileToS3(String fileUrl, PortalUser portalUser) throws SystemException;
    String uploadFileToS3(String fileUrl, Integer userId, Integer masterId) throws SystemException;
}
