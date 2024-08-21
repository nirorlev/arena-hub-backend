package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data transfer object representing license permissions")
public class LicensePermissionsDto {
    @ApiModelProperty(value = "Limit of channels publishing for current user org")
    private Integer publishedChannelLimit;
    @ApiModelProperty(value = "Limit of playlists publishing for current user org")
    private Integer publishedPlaylistLimit;
}
