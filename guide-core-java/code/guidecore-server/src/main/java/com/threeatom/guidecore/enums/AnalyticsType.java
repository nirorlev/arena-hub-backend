package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AnalyticsType {
    CHANNEL_COUNT("Channel count"),
    VIDEO_COUNT("Video count"),
    PLAYLIST_COUNT("Playlist count"),
    VIDEO_VIEW_COUNT("Video view count"),
    VIDEO_WATCHING_TIME("Video watching time"),
    AVERAGE_VIDEO_WATCHING_TIME("Average video watching time"),
    VIEWERS_COUNT("Viewers count"),
    DROP_OFF_RATE("Drop off rate"),
    ENGAGEMENT_RATE("Engagement rate");

    private final String label;
}
