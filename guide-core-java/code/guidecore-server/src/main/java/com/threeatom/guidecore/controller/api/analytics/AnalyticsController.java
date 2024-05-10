package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.util.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
public class AnalyticsController {

    private final AnalyticsFacade analyticsFacade;

    @GetMapping("/channel-count")
    public ResponseEntity<AnalyticsResponseDto> channelCount(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getChannelsCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-count")
    public ResponseEntity<AnalyticsResponseDto> videoCount(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoCountAnalytics(filter, masterId));
    }

    @GetMapping("/playlist-count")
    public ResponseEntity<AnalyticsResponseDto> playlistCount(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getPlaylistCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-view-count")
    public ResponseEntity<AnalyticsResponseDto> videoViewCount(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoViewCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-watching-time")
    public ResponseEntity<AnalyticsResponseDto> videoWatchingTime(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoWatchingTimeAnalytics(filter, masterId));
    }

    @GetMapping("/average-video-watching-time")
    public ResponseEntity<AnalyticsResponseDto> averageVideoWatchingTime(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getAverageVideoWatchingTimeAnalytics(filter, masterId));
    }

    @GetMapping("/viewers-count")
    public ResponseEntity<AnalyticsResponseDto> viewersCount(@Valid AnalyticsFilterDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getViewersCountAnalytics(filter, masterId));
    }
}
