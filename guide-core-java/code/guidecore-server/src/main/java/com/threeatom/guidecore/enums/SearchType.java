package com.threeatom.guidecore.enums;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SearchType {
    VIDEO(1),
    COURSE(2),
    CHANNEL(5),
    PLAYLIST(6),
    ALL(7);

    private final int value;

    public static SearchType fromValue(int value) {
        return Arrays.stream(SearchType.values())
            .filter(type -> type.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid search type value: " + value));
    }
}
