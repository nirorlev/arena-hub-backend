package com.threeatom.guidecore.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSessionDto {
    @NotNull(message = "Id must not be null")
    private UUID id;

    @NotNull(message = "Start time must not be null")
    private OffsetDateTime startTime;

    @NotNull(message = "Task duration must not be null")
    @Min(value = 0, message = "Duration must be greater than or equal to 0")
    private Integer duration;
}
