package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a video search elements")
public class VideoSearchResponseDto {
    @ApiModelProperty(value = "List of video search results")
    private List<VideoSearchResultDto> result;
}
