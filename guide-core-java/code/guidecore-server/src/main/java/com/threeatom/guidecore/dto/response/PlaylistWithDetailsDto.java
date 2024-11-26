package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Data Transfer Object representing a Channel")
@Getter
@Setter
public class PlaylistWithDetailsDto extends PlaylistDto {
    @ApiModelProperty(value = "Permissions of the current user on the playlist")
    private Map<String, Boolean> permissions;
    @ApiModelProperty(value = "Privacy visibility for sharable button")
    private Boolean isPrivate;
    @ApiModelProperty(value = "URL of the playlist's background")
    private String snapshotUrl;
    @ApiModelProperty(value = "Total number of videos in the playlist")
    private Integer videoNum;

    @ApiModelProperty(value = "Playlist content items")
    private List<ContentDto> saveContentList;
    @ApiModelProperty(value = "datetime of user subscription to the playlist")
    private OffsetDateTime subscriptionTime;
}