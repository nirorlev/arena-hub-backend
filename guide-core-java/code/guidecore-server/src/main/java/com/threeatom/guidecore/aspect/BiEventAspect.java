package com.threeatom.guidecore.aspect;

import com.threeatom.guidecore.aspect.annotation.BiEvent;
import com.threeatom.guidecore.service.BiEventService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;

@RequiredArgsConstructor
public class BiEventAspect {

    private final BiEventService biEventService;

    @Pointcut("@annotation(biEventAnnotation)")
    public void callAt(BiEvent biEventAnnotation) {
    }

    @AfterReturning("callAt(biEventAnnotation)")
    public void afterCallAt(JoinPoint joinPoint, BiEvent biEventAnnotation) {
        biEventService.send(biEventAnnotation.eventType());
    }
}
