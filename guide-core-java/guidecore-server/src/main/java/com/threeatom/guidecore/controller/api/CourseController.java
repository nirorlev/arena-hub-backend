package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.dto.response.AssignedCourseDto;
import com.threeatom.guidecore.dto.response.CourseDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.CourseListDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.CourseVideoBookmarkDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentProgressService;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CoursePreviewProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.CourseService;
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
    private final CourseService courseService;
    private final GcVideoService videoService;
    private final GcUserService userService;
    private final PortalUserService portalUserService;
    private final CourseSettingService courseSettingService;
    private final CourseEnrollmentProgressService courseEnrollmentProgressService;
    private final CoursePreviewProgressService coursePreviewProgressService;

    @GetMapping("/assigned")
    public ResponseEntity<CourseListDto<AssignedCourseDto>> userAssignedCourses(HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        return ResponseEntity.ok(courseService.getAssignedCourses(portalUser));
    }

    @GetMapping("/owned")
    public ResponseEntity<CourseListDto<CourseDto>> userOwnedCourses(HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        return ResponseEntity.ok(courseService.getOwnedCourses(portalUser));
    }

    @GetMapping("/discoverable")
    public ResponseEntity<CourseListDto<CourseDto>> discoverableCourses(HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        return ResponseEntity.ok(courseService.getDiscoverableCourses(portalUser));
    }

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
        return ResponseEntity.ok(
            courseEnrollmentProgressService.courseProgress(courseId, getPortalUser(request)));
    }

    @GetMapping("/{courseId}/progress-preview")
    public ResponseEntity<CourseProgressDto> courseProgressPreview(@PathVariable Integer courseId,
                                                                   HttpServletRequest request) {
        return ResponseEntity.ok(
            coursePreviewProgressService.courseProgress(courseId, getPortalUser(request)));
    }

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<CourseEnrollmentsDto> courseEnrollments(@PathVariable Integer courseId,
                                                                  @RequestParam(value = "startDate", required = false)
                                                                  OffsetDateTime startDate,
                                                                  @RequestParam(value = "endDate", required = false)
                                                                  OffsetDateTime endDate,
                                                                  @RequestParam(value = "users", required = false, defaultValue = "me")
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

    @GetMapping("/{courseId}/bookmarks/last-viewed")
    public ResponseEntity<CourseVideoBookmarkDto> lastViewedBookmark(@PathVariable Integer courseId,
                                                                     HttpServletRequest request) {
        return ResponseEntity.ok(courseService.lastViewedBookmark(courseId, getPortalUser(request)));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> courseDetails(@PathVariable Integer courseId, HttpServletRequest request) {
        return ResponseEntity.ok(courseService.getCourseDetails(courseId, getPortalUser(request)));
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
