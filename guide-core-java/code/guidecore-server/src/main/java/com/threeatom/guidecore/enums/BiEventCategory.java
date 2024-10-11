package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BiEventCategory {
    BACKEND_USER_EVENT("backend_user_event");

    private final String value;
}
