package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultViewPerSecondDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoViewPerSecondDto;
import com.threeatom.guidecore.dto.request.VideoViewerDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.AnalyticsAggregation;
import com.threeatom.guidecore.enums.AnalyticsType;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsFacadeImpl implements AnalyticsFacade {

    private final Map<AnalyticsType, BiFunction<AnalyticsFilterDto, PortalUser, AnalyticsResponseDto>>
        analyticsTypeAnalyticsResponseDtoMap = new HashMap<>();

    private final PtChannelService channelService;
    private final GcUserSaveFolderService userSaveFolderService;
    private final VideoPlaySessionService videoPlaySessionService;
    private final VideoPlaySegmentService videoPlaySegmentService;
    private final AuthorizationService authorizationService;

    @Lazy
    @Autowired
    private GcVideoService videoService;

    @PostConstruct
    public void init() {
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.CHANNEL_COUNT, this::getChannelsCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIDEO_COUNT, this::getVideoCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.PLAYLIST_COUNT, this::getPlaylistCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIDEO_VIEW_COUNT, this::getVideoViewCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIDEO_WATCHING_TIME,
            this::getVideoWatchingTimeAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.AVERAGE_VIDEO_WATCHING_TIME,
            this::getAverageVideoWatchingTimeAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIEWERS_COUNT, this::getViewersCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.DROP_OFF_RATE, this::getDropOffRateAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.ENGAGEMENT_RATE, this::getEngagementRateAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.LIKES, this::getLikesAnalytics);
    }

    @Override
    public AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        checkPermission(portalUser);
        List<DbAnalyticsResultDto> analytics =
            channelService.getChannelsCountAnalytics(filter, portalUser.getMasterId());

        return getAnalyticsResponseDto(analytics, AnalyticsType.CHANNEL_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        List<DbAnalyticsResultDto> analytics = videoService.getVideoCountAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        List<DbAnalyticsResultDto> analytics =
            userSaveFolderService.getPlaylistCountAnalytics(filter, portalUser.getMasterId());

        return getAnalyticsResponseDto(analytics, AnalyticsType.PLAYLIST_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = videoPlaySessionService.getVideoViewCountAnalytics(filter,
                portalUser.getMasterId());

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_VIEW_COUNT.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> videoViewCountAnalytics =
            videoPlaySessionService.getVideoViewCountByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(videoViewCountAnalytics, AnalyticsType.VIDEO_VIEW_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics =
                videoPlaySegmentService.getVideoWatchingTimeAnalytics(filter, portalUser.getMasterId());

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_WATCHING_TIME.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> videoWatchingTimeAnalytics =
            videoPlaySegmentService.getVideoWatchingTimeByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(videoWatchingTimeAnalytics, AnalyticsType.VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics =
                videoPlaySessionService.getViewersCountAnalytics(filter, portalUser.getMasterId());

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIEWERS_COUNT.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> viewersCountAnalytics =
            videoPlaySessionService.getViewersCountByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(viewersCountAnalytics, AnalyticsType.VIEWERS_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        List<DbAnalyticsResultDto> analytics =
            videoPlaySegmentService.getAverageVideoWatchingTimeAnalytics(filter, portalUser.getMasterId());

        return getAnalyticsResponseDto(analytics, AnalyticsType.AVERAGE_VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getDropOffRateAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics =
                videoPlaySegmentService.getDropOffRateAnalytics(filter, portalUser.getMasterId());

            return getAnalyticsResponseDto(analytics, AnalyticsType.DROP_OFF_RATE.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> dropOffRateAnalytics =
            videoPlaySegmentService.getDropOffRateByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(dropOffRateAnalytics, AnalyticsType.DROP_OFF_RATE.getLabel());
    }

    @Override
    public AnalyticsResponseDto getEngagementRateAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = videoPlaySegmentService.getEngagementRateAnalytics(filter,
                portalUser.getMasterId());

            return getAnalyticsResponseDto(analytics, AnalyticsType.ENGAGEMENT_RATE.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> engagementRateAnalytics =
            videoPlaySegmentService.getEngagementRateByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(engagementRateAnalytics, AnalyticsType.ENGAGEMENT_RATE.getLabel());
    }

    @Override
    public Map<Integer, String> getVideoIdAnalytics(AnalyticsFilterDto filter, AnalyticsType analyticsType,
                                                    PortalUser portalUser) {
        checkPermission(portalUser);

        AnalyticsResponseDto<Integer, String> analytics =
            analyticsTypeAnalyticsResponseDtoMap.get(analyticsType).apply(filter, portalUser);

        return analytics.getResult().get(0).getValues().stream()
            .collect(Collectors.toMap(MetricValuePairDto::getX, MetricValuePairDto::getY));
    }

    @Override
    public AnalyticsResponseDto getLikesAnalytics(AnalyticsFilterDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        List<DbAnalyticsResultVideoIdDto> likesAnalytics =
            videoService.getLikesByVideoAnalytics(filter, portalUser.getMasterId());
        return getAnalyticsByVideoResponseDto(likesAnalytics, AnalyticsType.LIKES.getLabel());
    }

    @Override
    public VideoViewersDto videoViewers(VideoViewerDetailsDto filter, PortalUser portalUser) {
        checkPermission(portalUser);

        VideoViewersDto videoViewersDto = new VideoViewersDto();
        videoViewersDto.setResults(videoPlaySessionService.getVideoViewersAnalytics(filter, portalUser.getMasterId()));
        return videoViewersDto;
    }

    @Override
    public AnalyticsResponseDto<String, String> videoViewsPerSecondAnalytics(VideoViewPerSecondDto filter,
                                                                             PortalUser portalUser) {
        checkPermission(portalUser);

        List<DbAnalyticsResultViewPerSecondDto> viewPerSecondAnalytics =
            videoPlaySegmentService.videoViewsPerSecondAnalytics(filter, portalUser.getMasterId());

        AnalyticsResponseDto<String, String> analyticsResponseDto = new AnalyticsResponseDto<>();
        List<MetricValuePairDto<String, String>> metricValuePair = viewPerSecondAnalytics.stream()
            .map(entry -> createMetricValuePairDto(entry.getSecond(), entry.getValue()))
            .collect(Collectors.toList());
        analyticsResponseDto.setResult(List.of(createResultDto(metricValuePair, "Video views per second")));

        return analyticsResponseDto;
    }

    private AnalyticsResponseDto getAnalyticsResponseDto(List<DbAnalyticsResultDto> analyticsCountResults,
                                                         String metricName) {
        AnalyticsResponseDto analyticsResponseDto = new AnalyticsResponseDto();
        ResultDto<OffsetDateTime, String> resultDto = createResulDateDto(analyticsCountResults, metricName);
        analyticsResponseDto.setResult(Collections.singletonList(resultDto));
        return analyticsResponseDto;
    }

    private AnalyticsResponseDto<Integer, String> getAnalyticsByVideoResponseDto(
        List<DbAnalyticsResultVideoIdDto> analyticsCountResults, String metricName) {
        AnalyticsResponseDto<Integer, String> analyticsResponseDto = new AnalyticsResponseDto<>();
        ResultDto<Integer, String> resultDto = createResultByVideoIdDto(analyticsCountResults, metricName);
        analyticsResponseDto.setResult(Collections.singletonList(resultDto));
        return analyticsResponseDto;
    }

    private ResultDto<OffsetDateTime, String> createResulDateDto(List<DbAnalyticsResultDto> analyticsCountResults,
                                                                 String metricName) {
        List<MetricValuePairDto<OffsetDateTime, String>> metricValuePair = analyticsCountResults.stream()
            .map(entry -> createMetricValuePairDto(entry.getTimeBucket(), String.valueOf(entry.getValue())))
            .collect(Collectors.toList());

        return createResultDto(metricValuePair, metricName);
    }

    private ResultDto<Integer, String> createResultByVideoIdDto(List<DbAnalyticsResultVideoIdDto> analyticsCountResults,
                                                                String metricName) {
        List<MetricValuePairDto<Integer, String>> metricValuePair = analyticsCountResults.stream()
            .map(entry -> createMetricValuePairDto(entry.getVideoId(), String.valueOf(entry.getValue())))
            .collect(Collectors.toList());

        return createResultDto(metricValuePair, metricName);
    }

    private <T, U> ResultDto<T, U> createResultDto(List<MetricValuePairDto<T, U>> metricValuePairs,
                                                   String metricName) {
        ResultDto<T, U> resultDto = new ResultDto<>();
        MetricDto metricDto = new MetricDto();
        metricDto.setName(metricName);
        resultDto.setMetric(metricDto);
        resultDto.setValues(metricValuePairs);
        return resultDto;
    }

    private <T, V> MetricValuePairDto<T, V> createMetricValuePairDto(T xValue, V yValue) {
        MetricValuePairDto<T, V> metricValuePairDto = new MetricValuePairDto<>();
        metricValuePairDto.setX(xValue);
        metricValuePairDto.setY(yValue);
        return metricValuePairDto;
    }

    private void checkPermission(PortalUser portalUser) {
        if (!authorizationService.checkAccess(PortalAction.ACCESS_ANALYTICS, portalUser)) {
            throw new ForbiddenException("No permission to access analytics");
        }
    }
}
