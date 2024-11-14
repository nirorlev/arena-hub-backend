package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.Map;
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

    @ApiModelProperty(value = "Permissions of the current user on the playlist")
    private Map<String, Boolean> permissions;

    @ApiModelProperty(value = "Privacy visibility for sharable button")
    private Boolean isPrivate;

    @ApiModelProperty(value = "Owner of the playlist")
    private UserDetailsDto owner;

    @ApiModelProperty(value = "URL of the playlist's background")
    private String snapshotUrl;

    @ApiModelProperty(value = "Total number of videos in the playlist")
    private Integer videoNum;

    @ApiModelProperty(value = "Creation time of the playlist")
    private OffsetDateTime createTime;

    @ApiModelProperty(value = "Last update time of the playlist")
    private OffsetDateTime updateTime;
}