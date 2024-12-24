package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.ContentGroupsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.RequestUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/api/v2/content-groups",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContentGroupController {

    private final GcContentGroupCourseAssignmentService gcContentGroupCourseAssignmentService;
    private final ContentGroupChannelSubscriptionService contentGroupChannelSubscriptionService;
    private final GcAccessService contentGroupService;
    private final GcUserService gcUserService;

    @GetMapping("/{content-group-id}/course-assignments")
    public ResponseEntity<List<ContentGroupCourseAssignmentDto>> getCourseAssignment(
        @PathVariable("content-group-id") Integer contentGroupId, HttpServletRequest request) {
        return ResponseEntity.ok()
            .body(gcContentGroupCourseAssignmentService.findByContentGroupId(contentGroupId, request));
    }

    @PostMapping(value = "/course/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignCourse(@RequestBody AssignCourseDto assignCourseDto, HttpServletRequest request) {
        gcContentGroupCourseAssignmentService.assignCourse(gcUserService.getCurrentUser(request), assignCourseDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/courses/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignCourses(@RequestBody List<AssignCourseDto> assignCourseDto,
                                              HttpServletRequest request) {
        gcContentGroupCourseAssignmentService.assignCourses(gcUserService.getCurrentUser(request), assignCourseDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/course-assignments/{assignment-id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateCourseAssignment(
        @PathVariable("assignment-id") Integer courseAssignmentId, @RequestBody AssignCourseDto assignCourseDto,
        HttpServletRequest request) {
        gcContentGroupCourseAssignmentService.updateCourseAssignment(
            courseAssignmentId, gcUserService.getCurrentUser(request), assignCourseDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/course-assignments/remove/{assignment-id}")
    public ResponseEntity<Void> removeCourseFromContentGroup(
        @PathVariable("assignment-id") Integer courseAssignmentId) {
        gcContentGroupCourseAssignmentService.removeCourseAssignment(courseAssignmentId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{content-group-id}/channel-subscriptions")
    public ResponseEntity<List<ContentGroupChannelSubscriptionDto>> getChannelSubscriptions(
        @PathVariable("content-group-id") Integer contentGroupId, HttpServletRequest request) {
        return ResponseEntity.ok()
            .body(contentGroupChannelSubscriptionService.getContentGroupSubscriptions(contentGroupId, request));
    }

    @GetMapping
    public ResponseEntity<ContentGroupsDto> userContentGroups(HttpServletRequest request) {
        GcUser currentUser = gcUserService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        return ResponseEntity.ok().body(contentGroupService.userContentGroups(currentUser.getId(), masterId));
    }

}
