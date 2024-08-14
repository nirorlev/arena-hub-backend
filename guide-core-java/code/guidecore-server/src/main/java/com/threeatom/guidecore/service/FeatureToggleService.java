package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.FeatureToggle;
import java.util.List;

public interface FeatureToggleService extends IService<FeatureToggle> {

    FeatureToggleDto getAllDefaultFeatures();

    FeatureToggleDto getAllFeatureToggles(Integer masterId);

    List<FeatureToggleValueDto> getAllFeatureToggles();

    FeatureToggleValueDto getFeatureToggle(String featureKey);

    FeatureToggleValueDto getFeatureToggle(String featureKey, Integer masterId);

    void updateFeatureToggle(FeatureToggleValueDto featureToggleValueDto);
}
