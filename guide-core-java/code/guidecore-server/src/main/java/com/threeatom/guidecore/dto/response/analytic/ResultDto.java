package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a result")
public class ResultDto {
    @ApiModelProperty(value = "Metric data")
    private MetricDto metric;

    @ApiModelProperty(value = "List of values")
    private List<MetricValuePairDto<OffsetDateTime, String>> values;
}