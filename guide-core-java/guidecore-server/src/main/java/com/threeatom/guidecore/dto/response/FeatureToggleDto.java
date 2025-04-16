package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a feature toggle")
public class FeatureToggleDto {
    @ApiModelProperty(notes = "The key-value pairs of feature toggles and their values")
    private Map<String, String> features;
}
