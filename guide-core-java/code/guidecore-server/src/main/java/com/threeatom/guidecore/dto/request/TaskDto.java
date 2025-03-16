package com.threeatom.guidecore.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    @NotNull(message = "Video time is required")
    @Min(value = 0, message = "Video time must be greater or equals to 0")
    private Integer timestamp;

    @Min(value = 0, message = "Order number must be greater than or equal to 0")
    private Integer order;

    @Valid
    private QuestionDto question;

    @NotNull
    @Min(value = -1, message = "Task retries must be greater than or equals to -1 (unlimited)")
    private Integer retries = -1;

    private Boolean canSkip = true;
}
