package com.threeatom.common.validation.annotation;

import com.threeatom.common.validation.ValidateVideoPlayTimeOrder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidateVideoPlayTimeOrder.class)
public @interface ValidateTimeOrder {
    String message() default "Please follow time order rule";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
