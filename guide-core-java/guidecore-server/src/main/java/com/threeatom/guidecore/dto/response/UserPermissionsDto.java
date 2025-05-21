package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(description = "Data transfer object representing license permissions")
public class UserPermissionsDto {
    @ApiModelProperty(value = "Limit of channels publishing for current user org")
    private int publishedChannelLimit;
    @ApiModelProperty(value = "Limit of playlists publishing for current user org")
    private int publishedPlaylistLimit;
    @ApiModelProperty(value = "Flag to indicate if user can access teams")
    private boolean canAccessTeams;
}
