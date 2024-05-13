package com.threeatom.guidecore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureToggleValueDto {
    private String name;
    private String description;
    private Integer masterId;
    private String value;
}
