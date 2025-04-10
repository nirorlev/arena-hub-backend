package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseState {
    PRIVATE(0),
    CERTAIN_TEAMS(1);

    private final Integer value;
}
