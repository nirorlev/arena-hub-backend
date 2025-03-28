package com.threeatom.common.permissions.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum VideoItemAction {
    CREATE("create"),
    DELETE("delete"),
    VIEW("view"),
    EDIT("edit"),
    SHARE("share"),
    RATE("rate"),
    COMMENT("comment");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
