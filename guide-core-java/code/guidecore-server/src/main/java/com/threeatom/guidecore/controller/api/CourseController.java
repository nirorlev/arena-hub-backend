package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseProgressService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Course")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/courses", produces = MediaType.APPLICATION_JSON_VALUE)
public class CourseController {

    private final CourseEnrollmentService courseEnrollmentService;
    private final GcSubjectService courseService;
    private final GcVideoService videoService;
    private final GcUserService userService;
    private final PortalUserService portalUserService;
    private final CourseProgressService courseProgressService;

    @PostMapping("/{courseId}/users")
    public ResponseEntity<Void> enrollToCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        courseEnrollmentService.enrollToCourse(portalUser, courseId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/videos/{videoId}")
    public ResponseEntity<VideoWithSourceDetailsDto<VideoSourceDto>> courseVideo(@PathVariable Integer courseId,
                                                                                 @PathVariable Integer videoId,
                                                                                 HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(videoService.courseVideo(courseId, videoId, portalUser));
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<CourseProgressDto> courseProgress(@PathVariable Integer courseId,
                                                            HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(courseProgressService.courseProgress(courseId, portalUser));
    }

    @GetMapping("/{courseId}/program")
    public ResponseEntity<CourseProgramDto> courseProgram(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(courseService.courseProgram(courseId, portalUser));
    }
}
