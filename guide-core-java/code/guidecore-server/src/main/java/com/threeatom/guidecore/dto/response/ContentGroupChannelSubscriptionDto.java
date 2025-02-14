package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group channel subscription")
public class ContentGroupChannelSubscriptionDto extends GroupChannelSubscriptionDto {

    @ApiModelProperty(notes = "The unique ID of the content group channel subscription")
    private Integer id;
}