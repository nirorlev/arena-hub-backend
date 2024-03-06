package com.threeatom.guidecore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CourseType {
    MANDATORY(0),
    OPTIONAL(1);

    private final int value;

    public static CourseType ofType(Integer type) {
        if (type == 0) {
            return MANDATORY;
        }
        return OPTIONAL;
    }

    public boolean isMandatory() {
        return this == MANDATORY;
    }
}
