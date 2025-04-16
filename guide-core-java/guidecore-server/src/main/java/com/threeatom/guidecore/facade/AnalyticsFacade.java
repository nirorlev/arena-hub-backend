package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoViewerDetailsDto;
import com.threeatom.guidecore.dto.request.VideoViewPerSecondDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.AnalyticsType;
import java.util.Map;

public interface AnalyticsFacade {
    AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, PortalUser masterId);

    AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, PortalUser masterId);

    AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getDropOffRateAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    AnalyticsResponseDto getEngagementRateAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    Map<Integer, String> getVideoIdAnalytics(AnalyticsFilterDto filter, AnalyticsType analyticsType, PortalUser portalUser);

    AnalyticsResponseDto getLikesAnalytics(AnalyticsFilterDto filter, PortalUser portalUser);

    VideoViewersDto videoViewers(VideoViewerDetailsDto filter, PortalUser portalUser);

    AnalyticsResponseDto<String, String> videoViewsPerSecondAnalytics(VideoViewPerSecondDto filter, PortalUser portalUser);
}
