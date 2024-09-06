package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseAvailabilityType {
    PUBLIC(1),
    CERTAIN_TEAMS(2),
    PRIVATE(4);

    private final Integer value;
}
