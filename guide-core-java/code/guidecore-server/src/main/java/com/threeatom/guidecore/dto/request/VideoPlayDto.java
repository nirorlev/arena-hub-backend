package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Video Play segment")
public class VideoPlayDto {
    @ApiModelProperty(value = "Session ID", required = true)
    private UUID sessionId;

    @ApiModelProperty(value = "Segment ID", required = true)
    private Integer segmentId;

    @ApiModelProperty(value = "Start Watch Time in Seconds", required = true)
    private Integer startWatchTimeInSeconds;

    @ApiModelProperty(value = "End Watch Time in Seconds", required = true)
    private Integer endWatchTimeInSeconds;
}