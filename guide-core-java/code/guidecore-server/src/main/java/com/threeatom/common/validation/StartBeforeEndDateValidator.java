package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.StartBeforeEndDate;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndDateValidator implements ConstraintValidator<StartBeforeEndDate, AnalyticsFilterDto> {
    @Override
    public boolean isValid(AnalyticsFilterDto dto, ConstraintValidatorContext context) {
        return dto.getStart().isBefore(dto.getEnd());
    }
}