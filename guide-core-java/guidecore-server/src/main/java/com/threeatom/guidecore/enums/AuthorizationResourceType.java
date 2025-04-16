package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthorizationResourceType {
    COURSE("Course"),
    VIDEO_ITEM("VideoItem"),
    PORTAL("Portal"),
    PLAYLIST("Playlist"),
    CHANNEL("Channel"),
    CONTENT_GROUP("ContentGroup");

    private final String value;
}
