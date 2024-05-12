package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing analytics response")
public class AnalyticsResponseDto {
    @ApiModelProperty(value = "List of results")
    private List<ResultDto> result;
}