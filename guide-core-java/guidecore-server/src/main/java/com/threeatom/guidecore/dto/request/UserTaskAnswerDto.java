package com.threeatom.guidecore.dto.request;

import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTaskAnswerDto {
    @NotNull(message = "answer cannot be null")
    @NotBlank(message = "answer cannot be blank")
    private String answer;
    @NotNull(message = "sessionId is required")
    private UUID sessionId;
    @NotNull(message = "Task version is required")
    @Min(value = 0, message = "Task version must be greater than or equal to 0")
    private Integer taskVersion;
}
