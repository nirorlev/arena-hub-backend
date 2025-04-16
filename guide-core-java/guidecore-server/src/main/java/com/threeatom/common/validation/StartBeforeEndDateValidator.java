package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.StartBeforeEndDate;
import com.threeatom.guidecore.dto.request.DateRangeDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndDateValidator implements ConstraintValidator<StartBeforeEndDate, DateRangeDto> {
    @Override
    public boolean isValid(DateRangeDto rangeDto, ConstraintValidatorContext context) {
        return rangeDto.getStart() != null && rangeDto.getStart().isBefore(rangeDto.getEnd());
    }
}
