package com.threeatom.guidecore.dto.response.analytic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a result of video search by a given query")
public class VideoSearchResultDto {
    @ApiModelProperty(value = "Id of a video")
    private Integer id;

    @ApiModelProperty(value = "Title of a video")
    private String title;

    @ApiModelProperty(value = "thumbnail url of a video")
    private String thumbNailUrl;
}
