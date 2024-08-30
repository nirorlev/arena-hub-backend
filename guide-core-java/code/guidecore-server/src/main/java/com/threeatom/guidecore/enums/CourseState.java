package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseState {
    DRAFT(0),
    PUBLISHED(1);

    private final Integer value;
}
