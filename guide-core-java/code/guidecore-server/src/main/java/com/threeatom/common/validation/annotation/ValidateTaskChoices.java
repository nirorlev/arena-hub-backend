package com.threeatom.common.validation.annotation;

import com.threeatom.common.validation.TaskChoicesValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskChoicesValidator.class)
public @interface ValidateTaskChoices {
    String message() default "Task choices are invalid for chosen type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}