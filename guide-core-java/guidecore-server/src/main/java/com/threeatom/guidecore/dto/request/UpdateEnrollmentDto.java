package com.threeatom.guidecore.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEnrollmentDto {
    @NotNull(message = "isActive cannot be null")
    private Boolean isActive;
}
