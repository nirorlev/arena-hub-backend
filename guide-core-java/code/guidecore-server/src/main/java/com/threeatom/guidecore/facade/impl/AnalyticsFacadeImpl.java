package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsFacadeImpl implements AnalyticsFacade {

    private final Map<AnalyticsType, BiFunction<AnalyticsFilterDto, Integer, AnalyticsResponseDto>>
        analyticsTypeAnalyticsResponseDtoMap = new HashMap<>();

    private final PtChannelService channelService;
    private final GcUserSaveFolderService userSaveFolderService;
    private final VideoPlaySessionService videoPlaySessionService;
    private final VideoPlaySegmentService videoPlaySegmentService;

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
    public AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
            () -> channelService.getTrendChannelsCountAnalytics(filter, masterId),
            () -> channelService.getChannelsCountAnalytics(filter, masterId));

        return getAnalyticsResponseDto(analytics, AnalyticsType.CHANNEL_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
            () -> videoService.getTrendVideoCountAnalytics(filter, masterId),
            () -> videoService.getVideoCountAnalytics(filter, masterId));

        return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
            () -> userSaveFolderService.getTrendPlaylistCountAnalytics(filter, masterId),
            () -> userSaveFolderService.getPlaylistCountAnalytics(filter, masterId));

        return getAnalyticsResponseDto(analytics, AnalyticsType.PLAYLIST_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
                () -> videoPlaySessionService.getTrendVideoViewCountAnalytics(filter, masterId),
                () -> videoPlaySessionService.getVideoViewCountAnalytics(filter, masterId));

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_VIEW_COUNT.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> videoViewCountAnalytics =
            videoPlaySessionService.getVideoViewCountByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(videoViewCountAnalytics, AnalyticsType.VIDEO_VIEW_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
                () -> videoPlaySegmentService.getTrendVideoWatchingTimeAnalytics(filter, masterId),
                () -> videoPlaySegmentService.getVideoWatchingTimeAnalytics(filter, masterId));

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIDEO_WATCHING_TIME.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> videoWatchingTimeAnalytics =
            videoPlaySegmentService.getVideoWatchingTimeByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(videoWatchingTimeAnalytics, AnalyticsType.VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
                () -> videoPlaySessionService.getTrendViewersCountAnalytics(filter, masterId),
                () -> videoPlaySessionService.getViewersCountAnalytics(filter, masterId));

            return getAnalyticsResponseDto(analytics, AnalyticsType.VIEWERS_COUNT.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> viewersCountAnalytics =
            videoPlaySessionService.getViewersCountByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(viewersCountAnalytics, AnalyticsType.VIEWERS_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
            () -> videoPlaySegmentService.getTrendAverageVideoWatchingTimeAnalytics(filter, masterId),
            () -> videoPlaySegmentService.getAverageVideoWatchingTimeAnalytics(filter, masterId));

        return getAnalyticsResponseDto(analytics, AnalyticsType.AVERAGE_VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getDropOffRateAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
                () -> videoPlaySegmentService.getTrendDropOffRateAnalytics(filter, masterId),
                () -> videoPlaySegmentService.getDropOffRateAnalytics(filter, masterId));

            return getAnalyticsResponseDto(analytics, AnalyticsType.DROP_OFF_RATE.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> dropOffRateAnalytics =
            videoPlaySegmentService.getDropOffRateByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(dropOffRateAnalytics, AnalyticsType.DROP_OFF_RATE.getLabel());
    }

    @Override
    public AnalyticsResponseDto getEngagementRateAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        if (AnalyticsAggregation.DATE.equals(filter.getAggregateBy())) {
            List<DbAnalyticsResultDto> analytics = getAnalytics(filter.getStep(),
                () -> videoPlaySegmentService.getTrendEngagementRateAnalytics(filter, masterId),
                () -> videoPlaySegmentService.getEngagementRateAnalytics(filter, masterId));

            return getAnalyticsResponseDto(analytics, AnalyticsType.ENGAGEMENT_RATE.getLabel());
        }

        List<DbAnalyticsResultVideoIdDto> engagementRateAnalytics =
            videoPlaySegmentService.getEngagementRateByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(engagementRateAnalytics, AnalyticsType.ENGAGEMENT_RATE.getLabel());
    }

    @Override
    public Map<Integer, String> getVideoIdAnalytics(AnalyticsFilterDto filter, AnalyticsType analyticsType,
                                                    Integer masterId) {
        AnalyticsResponseDto<Integer, String> analytics =
            analyticsTypeAnalyticsResponseDtoMap.get(analyticsType).apply(filter, masterId);

        return analytics.getResult().get(0).getValues().stream()
            .collect(Collectors.toMap(MetricValuePairDto::getX, MetricValuePairDto::getY));
    }

    @Override
    public AnalyticsResponseDto getLikesAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultVideoIdDto> likesAnalytics = videoService.getLikesByVideoAnalytics(filter, masterId);
        return getAnalyticsByVideoResponseDto(likesAnalytics, AnalyticsType.LIKES.getLabel());
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
            .map(entry -> createMetricValuePairDto(entry.getTimeBucket(), entry.getValue()))
            .collect(Collectors.toList());

        return createResultDto(metricValuePair, metricName);
    }

    private ResultDto<Integer, String> createResultByVideoIdDto(List<DbAnalyticsResultVideoIdDto> analyticsCountResults,
                                                                String metricName) {
        List<MetricValuePairDto<Integer, String>> metricValuePair = analyticsCountResults.stream()
            .map(entry -> createMetricValuePairDto(entry.getVideoId(), entry.getValue()))
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

    private <T> MetricValuePairDto<T, String> createMetricValuePairDto(T xValue, double yValue) {
        MetricValuePairDto<T, String> metricValuePairDto = new MetricValuePairDto<>();
        metricValuePairDto.setX(xValue);
        metricValuePairDto.setY(String.valueOf(yValue));
        return metricValuePairDto;
    }

    private boolean isTrend(Long step) {
        return step == null || step == 0;
    }

    private List<DbAnalyticsResultDto> getAnalytics(
        Long step, Supplier<List<DbAnalyticsResultDto>> trendAnalytics, Supplier<List<DbAnalyticsResultDto>> chartAnalytics) {
        if (isTrend(step)) {
            return trendAnalytics.get();
        }

        return chartAnalytics.get();
    }
}
