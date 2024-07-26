package com.threeatom.common.validation.annotation;

import com.threeatom.common.validation.IntTypeValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IntTypeValidator.class)
@Documented
public @interface IntTypeConstraint {

    String message() default "类型异常";

    int[] values() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
