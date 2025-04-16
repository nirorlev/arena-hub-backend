package com.threeatom.guidecore.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoViewPerSecondDto extends DateRangeDto {

    @NotNull(message = "Step cannot be null")
    @Positive(message = "Step must be positive number")
    private Long step;

    @NotNull(message = "Video ID cannot be null")
    @Positive(message = "Video ID must be positive number")
    private Integer videoId;
}
