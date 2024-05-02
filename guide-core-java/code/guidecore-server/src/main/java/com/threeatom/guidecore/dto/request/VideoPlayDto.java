package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.ValidateTimeOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Video Play segment")
@ValidateTimeOrder(message = "startWatchTimeInSeconds should be less than endWatchTimeInSeconds")
public class VideoPlayDto {
    @NotNull(message = "Session ID cannot be null")
    @ApiModelProperty(value = "Session ID", required = true)
    private UUID sessionId;

    @NotNull(message = "Segment ID cannot be null")
    @Min(value = 0, message = "Segment ID should be positive or 0")
    @ApiModelProperty(value = "Segment ID", required = true)
    private Integer segmentId;

    @NotNull(message = "startWatchTimeInSeconds cannot be null")
    @Min(value = 0, message = "Start Watch Time in Seconds should be positive or 0")
    @ApiModelProperty(value = "Start Watch Time in Seconds", required = true)
    private Integer startWatchTimeInSeconds;

    @NotNull(message = "End Watch Time in Seconds cannot be null")
    @Positive(message = "End Watch Time in Seconds should be positive number")
    @ApiModelProperty(value = "End Watch Time in Seconds", required = true)
    private Integer endWatchTimeInSeconds;
}