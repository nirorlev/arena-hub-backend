package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final GcUserService userService;
    private final PortalUserService portalUserService;

    @PostMapping("/{courseId}/users/")
    public ResponseEntity<Void> enrollToCourse(@PathVariable Integer courseId, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        courseEnrollmentService.enrollToCourse(portalUser, courseId);

        return ResponseEntity.ok().build();
    }
}