package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.AggregateByWithStep;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.enums.AnalyticsAggregation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class AggregateByWithStepValidator implements ConstraintValidator<AggregateByWithStep, AnalyticsFilterDto> {
    @Override
    public boolean isValid(AnalyticsFilterDto filter, ConstraintValidatorContext context) {
        return (AnalyticsAggregation.DATE.equals(filter.getAggregateBy()) && filter.getStep() != null
            && filter.getStep() >= 0) || !AnalyticsAggregation.DATE.equals(filter.getAggregateBy());
    }
}
