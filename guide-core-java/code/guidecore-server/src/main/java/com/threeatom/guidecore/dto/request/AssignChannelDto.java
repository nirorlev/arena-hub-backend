package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Channel Assignment")
public class AssignChannelDto {
    @ApiModelProperty(notes = "The unique ID of the channel to be assigned")
    private Integer channelId;
    @ApiModelProperty(notes = "The unique ID of the content group from which the channel is to be assigned")
    private Integer contentGroupId;
    @ApiModelProperty(notes = "Specifies whether the channel should be subscribed by user. Default is false")
    private Boolean subscribe = false;
}
