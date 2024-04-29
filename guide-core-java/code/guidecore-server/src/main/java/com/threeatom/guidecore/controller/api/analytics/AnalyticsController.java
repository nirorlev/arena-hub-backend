package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.response.analytic.AnalyticsCountDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.RequestUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {

    private final GcUserService userService;
    private final AnalyticsFacade analyticsFacade;

    @GetMapping("/channel-count")
    public ResponseEntity<AnalyticsCountDto> channelCount(
        @RequestParam(value = "start") @NotNull OffsetDateTime start,
        @RequestParam(value = "end", required = false) OffsetDateTime end,
        @RequestParam(value = "contentGroups", required = false) List<Integer> contentGroupIds,
        HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        return ResponseEntity.ok(
            analyticsFacade.channelsCount(contentGroupIds, start, end, masterId, userService.getCurrentUser(request)));
    }

    @GetMapping("/video-count")
    public ResponseEntity<AnalyticsCountDto> videoCount(
        @RequestParam(value = "start") @NotNull OffsetDateTime start,
        @RequestParam(value = "end", required = false) OffsetDateTime end,
        @RequestParam(value = "contentGroups", required = false) List<Integer> contentGroupIds,
        HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        return ResponseEntity.ok(
            analyticsFacade.videoCount(contentGroupIds, start, end, masterId, userService.getCurrentUser(request)));
    }

    @GetMapping("/playlist-count")
    public ResponseEntity<AnalyticsCountDto> playlistCount(
        @RequestParam(value = "start") @NotNull OffsetDateTime start,
        @RequestParam(value = "end", required = false) OffsetDateTime end,
        HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        return ResponseEntity.ok(
            analyticsFacade.playlistCount(start, end, masterId, userService.getCurrentUser(request)));
    }

    private AnalyticsResponseDto getAnalyticsResponseDto(String data, LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long minutes = duration.toMinutes();
        long step = minutes / 50;

        Random random = new Random();
        double currentValue = random.nextDouble() * 100;

        List<MetricValuePairDto<LocalDateTime, String>> values = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            LocalDateTime time = start.plusMinutes(i * step);
            currentValue += random.nextDouble() * 10 - 5;

            if (currentValue < 0) {
                currentValue = 0;
            }

            values.add(getMetricValuePairDto(time, currentValue));
        }

        MetricDto metric = new MetricDto();
        metric.setName(data);
        metric.setVideoId(1);

        ResultDto result = new ResultDto();
        result.setMetric(metric);
        result.setValues(values);

        List<ResultDto> results = new ArrayList<>();
        results.add(result);

        AnalyticsResponseDto response = new AnalyticsResponseDto();
        response.setResult(results);
        return response;
    }

    private MetricValuePairDto<LocalDateTime, String> getMetricValuePairDto(
        LocalDateTime time, double currentValue) {
        MetricValuePairDto<LocalDateTime, String> metricValuePairDto = new MetricValuePairDto<>();

        metricValuePairDto.setX(time);
        metricValuePairDto.setY(String.valueOf(currentValue));

        return metricValuePairDto;
    }
}
