package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import com.threeatom.guidecore.util.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v2/video", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VideoPlayController {

    private final GcUserService userService;
    private final VideoPlaySessionService videoPlaySessionService;
    private final VideoPlaySegmentService videoPlaySegmentService;

    @PostMapping(value = "/{videoId}/play-segment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsResponseDto> createVideoPlay(
        @PathVariable("videoId") Integer videoId,
        @NotNull @RequestBody VideoPlayDto videoPlayDto,
        HttpServletRequest request) {

        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        videoPlaySessionService.saveVideoPlaySession(videoPlayDto, currentUser, videoId, masterId);
        videoPlaySegmentService.saveVideoPlaySegment(videoPlayDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
