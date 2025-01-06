package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.SystemException;
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
    private final FeatureToggleService featureToggleService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Transactional(readOnly = true)
    @Override
    public String getVersion(String requestedVersion, String remoteHost) {
        try {
            if (StringUtils.isBlank(requestedVersion)) {
                return findLatestVersion();
            }

            if (requestedVersion.endsWith(LATEST_VERSION_IDENTIFIER)) {
                String version = requestedVersion.replace(LATEST_VERSION_IDENTIFIER, "");
                String latestDeployedVersion = findLatestDeployedVersion(version, remoteHost);
                return String.format("%s/%s", version, latestDeployedVersion);
            }

            return requestedVersion;
        } catch (Exception e) {
            log.error(String.format("Failed to get version for: %s and %s", requestedVersion, remoteHost), e);
            return "";
        }
    }

    private String findLatestDeployedVersion(String versionFolder, String remoteHost) {
        String fileUrl = String.format("%s%s/%s/latest_successful_build.txt", remoteHost, contextPath, versionFolder);

        byte[] bytes = FileUtil.retrieveFileFromUrl(fileUrl);
        String content = new String(bytes);
        log.info("Latest version from S3 for: {} is: {}", fileUrl, content);

        if (StringUtils.isBlank(content)) {
            String errorMessage = "Failed to find latest version from S3 for: " + versionFolder;
            log.error(errorMessage);
            throw new SystemException(errorMessage);
        }

        return content.split("\n")[0].trim();
    }

    private String findLatestVersion() {
        return featureToggleService.getFeatureToggle(FEATURE_TOGGLE_CONFIG_NAME).getValue();
    }
}