package com.threeatom.guidecore.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AnalyticsAggregation {
    DATE("view-date"),
    VIDEO_ID("video-id");

    private final String code;

    public static AnalyticsAggregation fromCode(String code) {
        for (AnalyticsAggregation analyticsAggregation : AnalyticsAggregation.values()) {
            if (analyticsAggregation.getCode().equals(code)) {
                return analyticsAggregation;
            }
        }

        throw new IllegalArgumentException("Unknown analytics aggregation code: " + code);
    }
}
