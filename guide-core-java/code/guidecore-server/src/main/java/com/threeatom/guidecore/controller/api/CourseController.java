package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import java.time.OffsetDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final CourseSettingService courseSettingService;

    @PostMapping("/{courseId}/enrollments")
    public ResponseEntity<UserCourseEnrollmentDto> addCourseEnrollment(@PathVariable Integer courseId,
                                                                       HttpServletRequest request) {
        return ResponseEntity.ok(courseEnrollmentService.enrollToCourse(courseId, getPortalUser(request)));
    }

    @PostMapping("/{courseId}/settings")
    public ResponseEntity<Void> saveCourseSetting(@PathVariable Integer courseId,
                                                  @RequestBody @Valid CourseSettingDto courseSetting,
                                                  HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        courseSettingService.save(courseId, courseSetting, portalUser);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{courseId}/settings")
    public ResponseEntity<Void> updateCourseSetting(@PathVariable Integer courseId,
                                                    @RequestBody @Valid CourseSettingDto courseSetting,
                                                    HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        courseSettingService.update(courseId, courseSetting, portalUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/videos/{videoId}")
    public ResponseEntity<VideoWithSourceDetailsDto<VideoSourceDto>> courseVideo(@PathVariable Integer courseId,
                                                                                 @PathVariable Integer videoId,
                                                                                 HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        return ResponseEntity.ok(videoService.courseVideo(courseId, videoId, portalUser));
    }

    @GetMapping("/{courseId}/progress")
    public ResponseEntity<CourseProgressDto> courseProgress(@PathVariable Integer courseId,
                                                            HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(courseProgressService.courseProgress(courseId, portalUser));
    }

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<CourseEnrollmentsDto> courseEnrollments(@PathVariable Integer courseId,
                                                                  @RequestParam(value = "startDate", required = false)
                                                                  OffsetDateTime startDate,
                                                                  @RequestParam(value = "endDate", required = false)
                                                                  OffsetDateTime endDate,
                                                                  @RequestParam(value = "users", required = false, defaultValue = "all")
                                                                  String users,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok(
            courseEnrollmentService.courseEnrollments(courseId, users, startDate, endDate, getPortalUser(request)));
    }

    @GetMapping("/{courseId}/program")
    public ResponseEntity<CourseProgramDto> courseProgram(@PathVariable Integer courseId, HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(courseService.courseProgram(courseId, portalUser));
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
