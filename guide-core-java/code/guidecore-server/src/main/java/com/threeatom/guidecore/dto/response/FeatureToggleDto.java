package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FeatureToggleDto {
    private Map<String, String> features;
}
