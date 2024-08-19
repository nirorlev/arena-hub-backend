package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ChannelVisibilityFlag {
    PRIVATE(0),
    PUBLIC(1),
    CERTAIN_TEAMS(2);

    private final Integer value;
}
