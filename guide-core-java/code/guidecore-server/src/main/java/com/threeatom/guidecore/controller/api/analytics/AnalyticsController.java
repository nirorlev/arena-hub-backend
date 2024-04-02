package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.MetricDto;
import com.threeatom.guidecore.dto.response.analytic.MetricValuePairDto;
import com.threeatom.guidecore.dto.response.analytic.ResultDto;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v2/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {

    @GetMapping
    public ResponseEntity<AnalyticsResponseDto> getAnalytics(
        @NotNull @RequestParam("data") String data,
        @NotNull @RequestParam(value = "start") String start,
        @RequestParam(value = "end", required = false) String end) {

        return ResponseEntity.ok().body(
            getAnalyticsResponseDto(
                data, LocalDateTime.parse(start), end == null ? LocalDateTime.now() : LocalDateTime.parse(end)));
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
