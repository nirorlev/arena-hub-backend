package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.FeatureToggle;

public interface FeatureToggleService extends IService<FeatureToggle> {
    FeatureToggleDto getAllFeatures();
}
