package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum BiEvent {
    LOGIN("login", "account", 290301, BiEventCategory.BACKEND_USER_EVENT);

    private final String action;
    private final String label;
    private final Integer value;
    private final BiEventCategory category;
}
