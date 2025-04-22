package com.threeatom.common.validation.annotation;

import com.threeatom.common.validation.TaskPropertiesValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskPropertiesValidator.class)
public @interface ValidateTaskProperties {
    String message() default "Task properties are invalid for specified task type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}