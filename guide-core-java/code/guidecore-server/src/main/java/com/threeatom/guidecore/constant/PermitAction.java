package com.threeatom.guidecore.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermitAction {
    DELETE("delete", "delete"),
    SHARE("share", "share"),
    CREATE("create", "create"),
    VIEW("view", "view"),
    COMMENT("comment", "comment"),
    EDIT("edit", "edit"),
    PUBLISH("publish", "publish"),
    ADD_CONTENT("addcontent", "addContent"),
    MANAGE_CONTENT("managecontent", "manageContent"),
    SUBSCRIBE("subscribe", "subscribe"),
    UNSUBSCRIBE("unsubscribe", "unsubscribe"),
    ACCESS_ANALYTICS("accessanalytics", "accessAnalytics"),
    ACCESS_TEAMS("accessteams", "accessTeams"),
    ACCESS_SETTINGS("accesssettings", "accessSettings"),
    ACCESS_CONFIG("accessconfig", "accessConfig");

    private final String value;
    private final String key;
}
