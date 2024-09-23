package com.threeatom.common.permissions.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelAction {
    CREATE("create"),
    DELETE("delete"),
    VIEW("view"),
    EDIT("edit"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    SHARE("share"),
    ADD_CONTENT("addContent"),
    MANAGE_CONTENT("manageContent"),
    PUBLISH("publish");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
