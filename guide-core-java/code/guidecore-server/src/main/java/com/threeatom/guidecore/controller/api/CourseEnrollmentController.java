package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.UpdateEnrollmentDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcUserService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Course")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/enrollments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CourseEnrollmentController {

    private final CourseEnrollmentService courseEnrollmentService;
    private final GcUserService userService;
    private final PortalUserService portalUserService;

    @GetMapping
    public ResponseEntity<CourseEnrollmentsDto> courseEnrollments(@RequestParam(value = "startDate", required = false)
                                                                  OffsetDateTime startDate,
                                                                  @RequestParam(value = "endDate", required = false)
                                                                  OffsetDateTime endDate,
                                                                  @RequestParam(value = "users", required = false, defaultValue = "me")
                                                                  String usersFlag,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok(
            courseEnrollmentService.courseEnrollments(usersFlag, startDate, endDate, getPortalUser(request)));
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<UserCourseEnrollmentDto> courseEnrollment(@PathVariable("enrollmentId") Long enrollmentId,
                                                                    HttpServletRequest request) {
        return ResponseEntity.ok(
            courseEnrollmentService.courseEnrollment(enrollmentId, getPortalUser(request)));
    }

    @PutMapping("/{enrollmentId}")
    public ResponseEntity<UserCourseEnrollmentDto> updateEnrollment(@PathVariable("enrollmentId") Long enrollmentId,
                                                                    @RequestBody @Valid
                                                                    UpdateEnrollmentDto updateEnrollmentDto,
                                                                    HttpServletRequest request) {
        return ResponseEntity.ok(
            courseEnrollmentService.updateCourseEnrollment(enrollmentId, updateEnrollmentDto, getPortalUser(request)));
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
