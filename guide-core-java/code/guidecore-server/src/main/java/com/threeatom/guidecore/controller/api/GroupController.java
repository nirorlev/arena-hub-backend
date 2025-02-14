package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.AssignChannelDto;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.GroupFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupController {

    private final GcUserService gcUserService;
    private final GroupFacade groupFacade;
    private final PortalUserService portalUserService;

    @GetMapping
    public ResponseEntity<GroupResponseDto> groups(HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(groupFacade.groups(portalUser));
    }

    @GetMapping("/{groupCode}/course-assignments")
    public ResponseEntity<List<ContentGroupCourseAssignmentDto>> courseAssignments(
        @PathVariable("groupCode") String groupCode, HttpServletRequest request) {
        return ResponseEntity.ok().body(groupFacade.groupCourseAssignments(groupCode, getPortalUser(request), request));
    }

    @PostMapping("/{groupCode}/course-assignments")
    public ResponseEntity<Void> assignCourse(@PathVariable("groupCode") String groupCode,
                                             @RequestBody AssignCourseDto assignCourseDto,
                                             HttpServletRequest request) {
        GcUser currentUser = gcUserService.getCurrentUser(request);
        groupFacade.assignCourseToGroup(groupCode, assignCourseDto, currentUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupCode}/channel-subscriptions")
    public ResponseEntity<List<ContentGroupChannelSubscriptionDto>> channelSubscriptions(
        @PathVariable("groupCode") String groupCode, HttpServletRequest request) {
        return ResponseEntity.ok()
            .body(groupFacade.groupChannelSubscriptions(groupCode, getPortalUser(request), request));
    }

    @PostMapping("/{groupCode}/channel-subscriptions")
    public ResponseEntity<Void> assignCourse(@PathVariable("groupCode") String groupCode,
                                             @RequestBody AssignChannelDto assignChannelDto,
                                             HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        groupFacade.assignChannelToGroup(groupCode, assignChannelDto, portalUser);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupCode}/channel-subscriptions/{channelId}")
    public ResponseEntity<Void> removeChannelSubscription(@PathVariable("groupCode") String groupCode,
                                             @PathVariable("groupCode") Integer channelId,
                                             HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        groupFacade.removeChannelSubscription(groupCode, channelId, portalUser);

        return ResponseEntity.ok().build();
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        GcUser currentUser = gcUserService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
