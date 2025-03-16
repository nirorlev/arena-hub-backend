package com.threeatom.guidecore.controller.api;


import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.VideoDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/videos", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Videos API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class VideoController {

    private final GcVideoService videoService;
    private final PortalUserService portalUserService;
    private final GcUserService userService;
    private final VideoEventFacade videoEventFacade;

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoDto> videoDetails(@PathVariable("videoId") Integer videoId, HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(videoService.getVideo(videoId, portalUser, request));
    }

    @GetMapping("/{videoId}/tasks")
    public ResponseEntity<List<TaskDto>> videoTasks(@PathVariable Integer videoId, HttpServletRequest request) {
        List<TaskDto> videoEvents = videoEventFacade.videoTasks(videoId, getPortalUser(request));
        return ResponseEntity.ok(videoEvents);
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
