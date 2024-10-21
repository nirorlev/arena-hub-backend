package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoListFilterDto;
import com.threeatom.guidecore.dto.request.VideoViewPerSecondDto;
import com.threeatom.guidecore.dto.request.VideoViewerDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.dto.response.analytic.VideoSearchResponseDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewersDto;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.service.GcVideoService;
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
    private final GcVideoService videoService;

    @GetMapping("/channel-count")
    public ResponseEntity<AnalyticsResponseDto> channelCount(@Valid AnalyticsFilterDto filter,
                                                             HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getChannelsCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-count")
    public ResponseEntity<AnalyticsResponseDto> videoCount(@Valid AnalyticsFilterDto filter,
                                                           HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoCountAnalytics(filter, masterId));
    }

    @GetMapping("/playlist-count")
    public ResponseEntity<AnalyticsResponseDto> playlistCount(@Valid AnalyticsFilterDto filter,
                                                              HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getPlaylistCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-view-count")
    public ResponseEntity<AnalyticsResponseDto> videoViewCount(@Valid AnalyticsFilterDto filter,
                                                               HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoViewCountAnalytics(filter, masterId));
    }

    @GetMapping("/video-watching-time")
    public ResponseEntity<AnalyticsResponseDto> videoWatchingTime(@Valid AnalyticsFilterDto filter,
                                                                  HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getVideoWatchingTimeAnalytics(filter, masterId));
    }

    @GetMapping("/average-video-watching-time")
    public ResponseEntity<AnalyticsResponseDto> averageVideoWatchingTime(@Valid AnalyticsFilterDto filter,
                                                                         HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getAverageVideoWatchingTimeAnalytics(filter, masterId));
    }

    @GetMapping("/viewers-count")
    public ResponseEntity<AnalyticsResponseDto> viewersCount(@Valid AnalyticsFilterDto filter,
                                                             HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getViewersCountAnalytics(filter, masterId));
    }

    @GetMapping("/drop-off-rate")
    public ResponseEntity<AnalyticsResponseDto> dropOffRate(@Valid AnalyticsFilterDto filter,
                                                            HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getDropOffRateAnalytics(filter, masterId));
    }

    @GetMapping("/engagement-rate")
    public ResponseEntity<AnalyticsResponseDto> engagementRate(@Valid AnalyticsFilterDto filter,
                                                               HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getEngagementRateAnalytics(filter, masterId));
    }

    @GetMapping("/video-likes-count")
    public ResponseEntity<AnalyticsResponseDto> videoLikes(@Valid AnalyticsFilterDto filter,
                                                           HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.getLikesAnalytics(filter, masterId));
    }

    @GetMapping("/video-viewer-list")
    public ResponseEntity<VideoViewersDto> videoViewers(@Valid VideoViewerDetailsDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.videoViewers(filter, masterId));
    }

    @GetMapping("/video-views-per-second")
    public ResponseEntity<AnalyticsResponseDto<Double, Double>> videoViewsPerSecond(
        @Valid VideoViewPerSecondDto filter, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(analyticsFacade.videoViewsPerSecondAnalytics(filter, masterId));
    }

    @GetMapping("/video-list")
    public ResponseEntity<VideoSearchResponseDto> engagementRete(@Valid VideoListFilterDto filter,
                                                                 HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok(videoService.getVideoListByQuery(filter, masterId, request));
    }
}
