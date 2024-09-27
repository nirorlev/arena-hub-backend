package com.threeatom.guidecore.aspect.annotation;

import com.threeatom.guidecore.enums.BiEventType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BiEvent {
    BiEventType eventType();
}