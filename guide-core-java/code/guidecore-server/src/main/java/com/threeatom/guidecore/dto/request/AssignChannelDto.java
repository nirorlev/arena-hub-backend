package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Channel Assignment")
public class AssignChannelDto {
    @ApiModelProperty(notes = "The unique ID of the channel to be assigned")
    @NotNull(message = "Channel ID is required")
    private Integer channelId;
    @ApiModelProperty(notes = "The unique ID of the content group from which the channel is to be assigned")
    @NotNull(message = "Content Group ID is required")
    private Integer contentGroupId;
    @ApiModelProperty(notes = "Specifies whether the channel should be auto-subscribed by user. Default is false")
    private Boolean autoSubscribe = false;
}
