package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.FrontendVersionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FrontendVersionServiceImpl implements FrontendVersionService {
    private static final String LATEST_VERSION_IDENTIFIER = "/latest";
    private static final String FEATURE_TOGGLE_CONFIG_NAME = "frontendVersion";

    private final AwsS3StorageService awsS3StorageService;
    private final FeatureToggleService featureToggleService;

    @Value("${aws.s3.frontendBucketName}")
    private String frontendBucketName;

    @Transactional(readOnly = true)
    public String getVersion(String requestedVersion, Integer masterId) {
        if (StringUtils.isBlank(requestedVersion)) {
            return findLatestVersion(masterId);
        }

        if (requestedVersion.endsWith(LATEST_VERSION_IDENTIFIER)) {
            String version = requestedVersion.replace(LATEST_VERSION_IDENTIFIER, "");
            String latestDeployedVersion = findLatestDeployedVersion(version, masterId);
            return String.format("%s/%s", version, latestDeployedVersion);
        }

        return requestedVersion;
    }

    private String findLatestDeployedVersion(String versionFolder, Integer masterId) {
        String filePath = String.format("%s/%s", frontendBucketName, versionFolder);
        byte[] bytes = awsS3StorageService.downloadFileFromS3UsingJetS3t(filePath, "latest_successful_build.txt");
        String content = new String(bytes);

        if (StringUtils.isBlank(content)) {
            return findLatestVersion(masterId);
        }

        return content.split("\n")[0].trim();
    }

    private String findLatestVersion(Integer masterId) {
        return featureToggleService.getFeatureToggle(FEATURE_TOGGLE_CONFIG_NAME, masterId).getValue();
    }
}