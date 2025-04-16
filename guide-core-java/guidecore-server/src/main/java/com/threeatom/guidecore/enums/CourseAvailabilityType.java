package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseAvailabilityType {
    PRIVATE(0),
    PUBLIC(1),
    CERTAIN_TEAMS(2);

    private final Integer value;
}
