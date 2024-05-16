package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsFacadeImpl implements AnalyticsFacade {

    private final PtChannelService channelService;
    private final GcVideoService videoService;
    private final GcUserSaveFolderService userSaveFolderService;
    private final VideoPlaySessionService videoPlaySessionService;
    private final VideoPlaySegmentService videoPlaySegmentService;

    @Override
    public AnalyticsResponseDto getChannelsCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> channelsCountAnalytics = channelService.getChannelsCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(channelsCountAnalytics, "Channels Count");
    }

    @Override
    public AnalyticsResponseDto getVideoCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoCountAnalytics = videoService.getVideoCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoCountAnalytics, "Videos Count");
    }

    @Override
    public AnalyticsResponseDto getPlaylistCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> playlistCountAnalytics = userSaveFolderService.getPlaylistCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(playlistCountAnalytics, "Playlist Count");
    }

    @Override
    public AnalyticsResponseDto getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoViewCountAnalytics = videoPlaySessionService.getVideoViewCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoViewCountAnalytics, "Video View Count");
    }

    @Override
    public AnalyticsResponseDto getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> videoWatchingTimeAnalytics = videoPlaySegmentService.getVideoWatchingTimeAnalytics(filter, masterId);
        return getAnalyticsResponseDto(videoWatchingTimeAnalytics, "Video Watching Time");
    }

    @Override
    public AnalyticsResponseDto getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> viewersCountAnalytics = videoPlaySessionService.getViewersCountAnalytics(filter, masterId);
        return getAnalyticsResponseDto(viewersCountAnalytics, "Viewers Count");
    }

    @Override
    public AnalyticsResponseDto getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        List<DbAnalyticsResultDto> averageVideoWatchingTimeAnalytics = videoPlaySegmentService.getAverageVideoWatchingTimeAnalytics(filter, masterId);
        return getAnalyticsResponseDto(averageVideoWatchingTimeAnalytics, "Average Video Watching Time");
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
