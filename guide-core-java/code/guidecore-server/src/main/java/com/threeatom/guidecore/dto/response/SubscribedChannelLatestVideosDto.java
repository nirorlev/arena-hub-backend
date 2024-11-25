package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Api("Data Transfer Object representing channel video with details")
public class SubscribedChannelLatestVideosDto extends VideoWithDetailsDto {
    @ApiModelProperty(value = "Related channel details")
    private ChannelDto channel;
}
