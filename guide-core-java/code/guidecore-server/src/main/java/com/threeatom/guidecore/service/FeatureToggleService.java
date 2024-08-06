package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.FeatureToggle;

public interface FeatureToggleService extends IService<FeatureToggle> {

    FeatureToggleDto getAllDefaultFeatures();

    FeatureToggleDto getAllFeatures(Integer masterId);

    FeatureToggleValueDto getFeatureToggle(String featureKey);

    FeatureToggleValueDto getFeatureToggle(String featureKey, Integer masterId);

    FeatureToggleDto updateFeatureToggle(FeatureToggleValueDto featureToggleValueDto);
}
