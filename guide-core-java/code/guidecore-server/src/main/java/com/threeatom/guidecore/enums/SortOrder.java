package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private final String code;

    public static SortOrder fromCode(String code) {
        for (SortOrder sortOrder : SortOrder.values()) {
            if (sortOrder.getCode().equals(code)) {
                return sortOrder;
            }
        }

        throw new IllegalArgumentException("Unknown sort order code: " + code);
    }
}
