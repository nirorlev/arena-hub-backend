package com.threeatom.guidecore.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermitAction {
    DELETE("delete"),
    SHARE("share"),
    CREATE("create"),
    VIEW("view"),
    COMMENT("comment"),
    EDIT("edit"),
    PUBLISH("publish"),
    ADD_CONTENT("addcontent"),
    MANAGE_CONTENT("managecontent"),
    SUBSCRIBE("subscribe"),
    ACCESS_ANALYTICS("accessanalytics"),
    ACCESS_TEAMS("accessteams");

    private final String value;
}
