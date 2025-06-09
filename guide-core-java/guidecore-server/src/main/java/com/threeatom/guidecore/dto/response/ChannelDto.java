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
public class ChannelDto extends BasicChannelDto {
    @ApiModelProperty(value = "Description of the Channel")
    private String desc;

    @ApiModelProperty(value = "Owner of the Channel")
    private UserDetailsDto owner;

    @ApiModelProperty(value = "Privacy visibility for sharable button")
    private Boolean isPrivate;

    @ApiModelProperty(value = "URL of the Channel's avatar")
    private String avatarUrl;

    @ApiModelProperty(value = "URL of the Channel's background")
    private String backgroundUrl;

    @ApiModelProperty(value = "Total number of subscribers of the Channel")
    private Integer totalSubscribers;

    @ApiModelProperty(value = "Permissions of the current user on the Channel")
    private Map<String, Boolean> permissions;

    @ApiModelProperty(value = "Time when user subscribed to the Channel")
    private OffsetDateTime subscriptionTime;

    @ApiModelProperty(value = "Creation time of the Channel")
    private OffsetDateTime createTime;

    @ApiModelProperty(value = "Last update time of the Channel")
    private OffsetDateTime updateTime;

    @ApiModelProperty(value = "Last update time of the Channel's content")
    private OffsetDateTime lastContentUpdateTime;
}