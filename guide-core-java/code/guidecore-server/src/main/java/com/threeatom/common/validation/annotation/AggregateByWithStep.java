package com.threeatom.common.validation.annotation;

import com.threeatom.common.validation.AggregateByWithStepValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AggregateByWithStepValidator.class)
public @interface AggregateByWithStep {
    String message() default "View date aggregation should be used with step attribute!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}