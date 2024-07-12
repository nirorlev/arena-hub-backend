package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ReactionType {
    LIKE(1);

    private final int reactionCode;
}
