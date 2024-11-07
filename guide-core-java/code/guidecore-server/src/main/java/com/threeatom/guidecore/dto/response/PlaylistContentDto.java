package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a playlist content")
public class PlaylistContentDto {
    @ApiModelProperty(value = "Unique identifier of the playlist content")
    private Integer id;
    @ApiModelProperty(value = "Unique identifier of the video")
    private Integer videoId;
}
