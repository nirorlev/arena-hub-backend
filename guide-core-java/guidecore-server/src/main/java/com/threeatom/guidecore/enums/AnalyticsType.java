package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AnalyticsType {
    CHANNEL_COUNT("Channel count", "channel-count"),
    VIDEO_COUNT("Video count", "video-count"),
    PLAYLIST_COUNT("Playlist count", "playlist-count"),
    VIDEO_VIEW_COUNT("Video view count", "video-view-count"),
    VIDEO_WATCHING_TIME("Video watching time", "video-watching-time"),
    AVERAGE_VIDEO_WATCHING_TIME("Average video watching time", "average-video-watching-time"),
    VIEWERS_COUNT("Viewers count", "viewers-count"),
    DROP_OFF_RATE("Drop off rate", "drop-off-rate"),
    ENGAGEMENT_RATE("Engagement rate", "engagement-rate"),
    LIKES("Likes count", "video-likes-count");

    private final String label;
    private final String sortCode;

    public static AnalyticsType fromSortCode(String sortCode) {
        for (AnalyticsType type : values()) {
            if (type.sortCode.equals(sortCode)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No AnalyticsType found for sort code: " + sortCode);
    }
}
