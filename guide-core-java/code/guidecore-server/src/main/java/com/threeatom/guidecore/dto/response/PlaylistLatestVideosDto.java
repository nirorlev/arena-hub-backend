package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Api("Data Transfer Object representing a video with details")
public class PlaylistLatestVideosDto extends VideoWithDetailsDto {
    @ApiModelProperty(value = "Related playlist details")
    private PlaylistDto playlist;
}
