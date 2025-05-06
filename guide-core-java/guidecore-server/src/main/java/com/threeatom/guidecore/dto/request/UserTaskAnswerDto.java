package com.threeatom.guidecore.dto.request;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTaskAnswerDto extends UserTaskAnswerPreviewDto {
    @NotNull(message = "sessionId is required")
    private UUID sessionId;
}
