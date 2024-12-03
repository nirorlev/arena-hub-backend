package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.FeVersionOverride;
import org.springframework.transaction.annotation.Transactional;

public interface FeVersionService extends IService<FeVersionOverride> {

    String getVersion(String value);

    String findLatestVersion();

    @Transactional
    void saveVersion(String version);
}