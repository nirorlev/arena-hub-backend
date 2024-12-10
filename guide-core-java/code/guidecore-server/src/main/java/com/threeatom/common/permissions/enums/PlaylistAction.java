package com.threeatom.common.permissions.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaylistAction {
    VIEW("view"),
    CREATE("create"),
    DELETE("delete"),
    EDIT("edit"),
    SHARE("share"),
    ADD_CONTENT("addContent"),
    MANAGE_CONTENT("manageContent"),
    PUBLISH("publish"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
