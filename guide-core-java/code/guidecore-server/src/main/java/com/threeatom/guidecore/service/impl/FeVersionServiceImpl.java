package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.FeVersionService;
import com.threeatom.guidecore.service.FeatureToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeVersionServiceImpl implements FeVersionService {
    private static final String LATEST = "/latest";
    private static final String FEATURE_TOGGLE_CONFIG_NAME = "feVersionOverride";

    private final AwsS3StorageService awsS3StorageService;
    private final FeatureToggleService featureToggleService;

    @Value("${aws.s3.feBucketName}")
    private String feBucketName;

    @Transactional(readOnly = true)
    public String getVersion(String versionValue, Integer masterId) {
        if (StringUtils.isBlank(versionValue)) {
            return findLatestVersion(masterId);
        }

        if (versionValue.endsWith(LATEST)) {
            String version = versionValue.replace(LATEST, "");
            String latestDeployedVersion = findLatestDeployedVersion(version, masterId);
            return String.format("%s/%s", version, latestDeployedVersion);
        }

        return versionValue;
    }

    private String findLatestDeployedVersion(String versionFolder, Integer masterId) {
        String filePath = String.format("%s/%s", feBucketName, versionFolder);
        byte[] bytes = awsS3StorageService.downloadFileFromS3UsingJetS3t(filePath, "latest_successful_build.txt");
        String content = new String(bytes);

        if (StringUtils.isBlank(content)) {
            return findLatestVersion(masterId);
        }

        return content.split("\n")[0].trim();
    }

    @Override
    public String findLatestVersion(Integer masterId) {
        return featureToggleService.getFeatureToggle(FEATURE_TOGGLE_CONFIG_NAME, masterId).getValue();
    }

    @Override
    public void updateVersion(String version, Integer masterId) {
        featureToggleService.updateFeatureToggle(FeatureToggleValueDto.builder()
            .name(FEATURE_TOGGLE_CONFIG_NAME)
            .value(version)
            .masterId(masterId)
            .build());
    }
}