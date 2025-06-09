package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Data Transfer Object representing a Channel")
@Getter
@Setter
public class PlaylistDto {
    @ApiModelProperty(value = "Unique identifier of the playlist")
    private Integer id;

    @ApiModelProperty(value = "Title of the playlist")
    private String name;

    @ApiModelProperty(value = "Owner of the playlist")
    private UserDetailsDto owner;

    @ApiModelProperty(value = "Playlist content size")
    private Integer size;

    @ApiModelProperty(value = "Creation time of the playlist")
    private OffsetDateTime createTime;

    @ApiModelProperty(value = "Last update time of the playlist")
    private OffsetDateTime updateTime;

    @ApiModelProperty(value = "Last update time of the playlist's content")
    private OffsetDateTime lastContentUpdateTime;
}