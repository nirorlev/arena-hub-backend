package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicChannelDto {
    @ApiModelProperty(value = "Unique identifier of the Channel")
    private Integer id;

    @ApiModelProperty(value = "Title of the Channel")
    private String title;

    @ApiModelProperty(value = "Slug of the Channel")
    private String slug;

    @ApiModelProperty(value = "URL of the Channel's avatar thumbnail")
    private String thumbUrl;
}
