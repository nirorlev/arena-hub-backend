package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.entity.FeatureToggle;
import org.mapstruct.Mapper;

@Mapper
public interface FeatureToggleMapping {

    FeatureToggleValueDto map(FeatureToggle featureToggle);
    FeatureToggle map(FeatureToggleValueDto featureToggle);
}
