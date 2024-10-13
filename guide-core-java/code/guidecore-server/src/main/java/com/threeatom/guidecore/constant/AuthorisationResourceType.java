package com.threeatom.guidecore.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthorisationResourceType {
    COURSE("Course"),
    VIDEO_ITEM("VideoItem"),
    PORTAL("Portal"),
    PLAYLIST("Playlist"),
    CHANNEL("Channel"),
    CONTENT_GROUP("ContentGroup");

    private final String value;
}
