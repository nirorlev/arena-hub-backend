package com.threeatom.guidecore.controller.api.analytics;

import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.response.analytic.AnalyticsResponseDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.util.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
@RequestMapping(value = "/api/v2/videos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VideoPlayController {

    private final GcUserService userService;
    private final VideoPlaySegmentService videoPlaySegmentService;
    private final PortalUserService portalUserService;

    @PostMapping(value = "/{videoId}/play-segments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsResponseDto> createVideoPlay(@PathVariable("videoId") Integer videoId,
                                                                @Valid @RequestBody VideoPlayDto videoPlayDto,
                                                                HttpServletRequest request) {
        videoPlaySegmentService.saveVideoPlaySegment(videoPlayDto, videoId, getPortalUser(request));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
