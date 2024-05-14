package com.threeatom.guidecore.dto.response.analytic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnalyticsCountDto {
    private int total;
    private int trendDifference;
}
