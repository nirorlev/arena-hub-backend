package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.FeVersionOverride;
import com.threeatom.guidecore.mapper.FeVersionOverrideMapper;
import com.threeatom.guidecore.service.FeVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeVersionServiceImpl extends ServiceImpl<FeVersionOverrideMapper, FeVersionOverride>
    implements FeVersionService {
    private static final String LATEST = "latest";

    @Transactional(readOnly = true)
    public String getVersion(String versionValue) {
        if (StringUtils.isBlank(versionValue)) {
            return "/" + findLatestVersion();
        }

        if (LATEST.equals(versionValue)) {
            log.info("Latest FE version requested");
        }

        return "/" + versionValue;
    }

    private String findLatestVersion() {
        QueryWrapper<FeVersionOverride> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("modified_date").last("LIMIT 1");
        FeVersionOverride latestVersion = this.getOne(queryWrapper);

        return latestVersion != null ? latestVersion.getVersion() : "";
    }
}