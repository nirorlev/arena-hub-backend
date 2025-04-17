package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group channel subscription")
public class GroupChannelSubscriptionDto {

    @ApiModelProperty(notes = "Group basic information")
    private BasicGroupDto group;

    @ApiModelProperty(notes = "User basic information")
    private UserDetailsDto user;

    @ApiModelProperty(notes = "Whether the channel should be auto-subscribed or not")
    private boolean autoSubscribe;

    @ApiModelProperty(notes = "The date and time when the content group course assignment was last modified")
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}