package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.FeVersionOverride;
import com.threeatom.guidecore.mapper.FeVersionOverrideMapper;
import com.threeatom.guidecore.service.AwsS3StorageService;
import com.threeatom.guidecore.service.FeVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeVersionServiceImpl extends ServiceImpl<FeVersionOverrideMapper, FeVersionOverride>
    implements FeVersionService {
    private static final String LATEST = "latest";
    private final AwsS3StorageService awsS3StorageService;
    @Value("${aws.s3.feBucketName}")
    private String feBucketName;

    @Transactional(readOnly = true)
    public String getVersion(String versionValue) {
        if (StringUtils.isBlank(versionValue)) {
            return "/" + findLatestVersion();
        }

        if (LATEST.equals(versionValue)) {
            return findLatestDeployedVersion();
        }

        return "/" + versionValue;
    }

    private String findLatestDeployedVersion() {
        byte[] bytes = awsS3StorageService.downloadFileFromS3UsingJetS3t(feBucketName, "latest_version.txt");
        String content = new String(bytes);

        if (StringUtils.isBlank(content)) {
            return findLatestVersion();
        }

        return content.split("\n")[0].trim();
    }

    private String findLatestVersion() {
        QueryWrapper<FeVersionOverride> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("modified_date").last("LIMIT 1");
        FeVersionOverride latestVersion = this.getOne(queryWrapper);

        return latestVersion != null ? latestVersion.getVersion() : "";
    }
}