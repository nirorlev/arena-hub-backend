package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.enums.AnalyticsType;
import java.util.Map;

public interface AnalyticsFacade {
    AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getDropOffRateAnalytics(AnalyticsFilterDto filter, Integer masterId);

    AnalyticsResponseDto getEngagementRateAnalytics(AnalyticsFilterDto filter, Integer masterId);

    Map<Integer, String> getVideoIdAnalytics(AnalyticsFilterDto filter, AnalyticsType analyticsType, Integer masterId);

    AnalyticsResponseDto getLikesAnalytics(AnalyticsFilterDto filter, Integer masterId);
}
