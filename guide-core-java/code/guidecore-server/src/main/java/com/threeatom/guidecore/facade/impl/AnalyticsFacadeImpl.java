package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
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

    private final Map<AnalyticsType, BiFunction<AnalyticsFilterDto, Integer, AnalyticsResponseDto>> analyticsTypeAnalyticsResponseDtoMap = new HashMap<>();

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
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIDEO_WATCHING_TIME, this::getVideoWatchingTimeAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.AVERAGE_VIDEO_WATCHING_TIME, this::getAverageVideoWatchingTimeAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.VIEWERS_COUNT, this::getViewersCountAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.DROP_OFF_RATE, this::getDropOffRateAnalytics);
        analyticsTypeAnalyticsResponseDtoMap.put(AnalyticsType.ENGAGEMENT_RATE, this::getEngagementRateAnalytics);
    }

    @Override
    public AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> channelsCountAnalytics = channelService.getChannelsCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(channelsCountAnalytics, AnalyticsType.CHANNEL_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoCountAnalytics = videoService.getVideoCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoCountAnalytics, AnalyticsType.VIDEO_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> playlistCountAnalytics = userSaveFolderService.getPlaylistCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(playlistCountAnalytics, AnalyticsType.PLAYLIST_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoViewCountAnalytics = videoPlaySessionService.getVideoViewCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoViewCountAnalytics, AnalyticsType.VIDEO_VIEW_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoWatchingTimeAnalytics = videoPlaySegmentService.getVideoWatchingTimeAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoWatchingTimeAnalytics, AnalyticsType.VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> viewersCountAnalytics = videoPlaySessionService.getViewersCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(viewersCountAnalytics, AnalyticsType.VIEWERS_COUNT.getLabel());
    }

    @Override
    public AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> averageVideoWatchingTimeAnalytics = videoPlaySegmentService.getAverageVideoWatchingTimeAnalytics(filter, masterId);
        return getAnalyticsResponseDto(averageVideoWatchingTimeAnalytics, AnalyticsType.AVERAGE_VIDEO_WATCHING_TIME.getLabel());
    }

    @Override
    public AnalyticsResponseDto getDropOffRateAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> dropOffRateAnalytics = videoPlaySegmentService.getDropOffRateAnalytics(filter, masterId);
        return getAnalyticsResponseDto(dropOffRateAnalytics, AnalyticsType.DROP_OFF_RATE.getLabel());
    }

    @Override
    public AnalyticsResponseDto getEngagementRateAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> engagementRateAnalytics = videoPlaySegmentService.getEngagementRateAnalytics(filter, masterId);
        return getAnalyticsResponseDto(engagementRateAnalytics, AnalyticsType.ENGAGEMENT_RATE.getLabel());
    }

    @Override
    public AnalyticsResponseDto getAnalytics(AnalyticsFilterDto filter, AnalyticsType analyticsType, Integer masterId) {
        return analyticsTypeAnalyticsResponseDtoMap.get(analyticsType).apply(filter, masterId);
    }

    private AnalyticsResponseDto getAnalyticsResponseDto(List<DbAnalyticsResultDto> analyticsCountResults, String metricName) {
        AnalyticsResponseDto analyticsResponseDto = new AnalyticsResponseDto();
        ResultDto resultDto = createResultDto(analyticsCountResults, metricName);
        analyticsResponseDto.setResult(Collections.singletonList(resultDto));
        return analyticsResponseDto;
    }

    private ResultDto createResultDto(List<DbAnalyticsResultDto> analyticsCountResults, String metricName) {
        ResultDto resultDto = new ResultDto();
        MetricDto metricDto = new MetricDto();
        metricDto.setName(metricName);

        List<MetricValuePairDto<OffsetDateTime, String>> metricValuePair = analyticsCountResults.stream()
            .map(this::createMetricValuePairDto)
            .collect(Collectors.toList());

        resultDto.setMetric(metricDto);
        resultDto.setValues(metricValuePair);
        return resultDto;
    }

    private MetricValuePairDto<OffsetDateTime, String> createMetricValuePairDto(DbAnalyticsResultDto entry) {
        MetricValuePairDto<OffsetDateTime, String> metricValuePairDto = new MetricValuePairDto<>();
        metricValuePairDto.setX(entry.getTimeBucket());
        metricValuePairDto.setY(String.valueOf(entry.getValue()));
        return metricValuePairDto;
    }
}
