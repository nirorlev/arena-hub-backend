package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a metric")
public class MetricDto {
    @ApiModelProperty(value = "Name of the metric", example = "VIDEO_VIEW")
    private String name;
}