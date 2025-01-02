package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.FrontendVersionService;
import com.threeatom.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FrontendVersionServiceImpl implements FrontendVersionService {
    private static final String LATEST_VERSION_IDENTIFIER = "/latest";
    private static final String FEATURE_TOGGLE_CONFIG_NAME = "frontendVersion";

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final FeatureToggleService featureToggleService;

    @Transactional(readOnly = true)
    public String getVersion(String requestedVersion, String remoteHost, Integer masterId) {
        if (StringUtils.isBlank(requestedVersion)) {
            return findLatestVersion(masterId);
        }

        if (requestedVersion.endsWith(LATEST_VERSION_IDENTIFIER)) {
            String version = requestedVersion.replace(LATEST_VERSION_IDENTIFIER, "");
            String latestDeployedVersion = findLatestDeployedVersion(version, remoteHost);
            return String.format("%s/%s", version, latestDeployedVersion);
        }

        return requestedVersion;
    }

    private String findLatestDeployedVersion(String versionFolder, String remoteHost) {
        String fileUrl = String.format("%s%s/%s/latest_successful_build.txt", remoteHost, contextPath, versionFolder);

        byte[] bytes = FileUtil.retrieveFileFromUrl(fileUrl);
        String content = new String(bytes);
        log.info("Latest version from S3 for: {} is: {}", fileUrl, content);

        if (StringUtils.isBlank(content)) {
            log.warn("Failed to find latest version from S3 for: {}", versionFolder);
            return null;
        }

        return content.trim().split("\n")[0];
    }

    private String findLatestVersion(Integer masterId) {
        return featureToggleService.getFeatureToggle(FEATURE_TOGGLE_CONFIG_NAME, masterId).getValue();
    }
}