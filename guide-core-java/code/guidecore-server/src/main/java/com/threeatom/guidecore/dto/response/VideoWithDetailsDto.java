package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Api("Data Transfer Object representing a video with details")
public class VideoWithDetailsDto extends VideoDto {
    @ApiModelProperty(value = "Number of likes")
    private Integer likesCount;
    @ApiModelProperty(value = "Whether the video is liked by the current user")
    private Boolean isLiked;
    @ApiModelProperty(value = "Video owner details")
    private UserDetailsDto owner;
    @ApiModelProperty(value = "Number of comments")
    private Integer commentsCount;
    @ApiModelProperty(value = "Related playlist details")
    private PlaylistDto playlist;

    @ApiModelProperty(value = "Permissions of the current user on the video")
    private Map<String, Boolean> permissions;
}
