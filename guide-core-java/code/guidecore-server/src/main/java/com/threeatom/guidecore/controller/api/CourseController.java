package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.IdDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/enroll")
    public ResponseEntity<Void> enrollToCourse(@RequestBody @Valid IdDto dto, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        courseEnrollmentService.enrollToCourse(portalUser, dto.getId());

        return ResponseEntity.ok().build();
    }
}