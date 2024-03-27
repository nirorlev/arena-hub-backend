package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a metric value pair")
public class MetricValuePairDto<X, Y> {
    @ApiModelProperty(value = "X-axis value", example = "2022-12-01T00:00:00")
    private X x;

    @ApiModelProperty(value = "Y-axis value", example = "100.0")
    private Y y;
}