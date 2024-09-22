package com.threeatom.common.permissions.service.impl;

import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import com.threeatom.common.permissions.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VideoItemAuthorizationServiceImpl extends ResourceAuthorizationService<GcVideo> {

    private final GcContentGroupCourseAssignmentService courseAssignmentService;
    private final ContentGroupChannelSubscriptionService channelSubscriptionService;

    public VideoItemAuthorizationServiceImpl(
        GcUserAccessService userAccessService,
        RoleBasedAuthorizationService ownerAuthorizationService,
        RoleBasedAuthorizationService orgAdminAuthorizationService,
        RoleBasedAuthorizationService adminAuthorizationService,
        RoleBasedAuthorizationService memberAuthorizationService,
        GcContentGroupCourseAssignmentService courseAssignmentService,
        ContentGroupChannelSubscriptionService channelSubscriptionService) {
        super(userAccessService, ownerAuthorizationService, orgAdminAuthorizationService, adminAuthorizationService,
            memberAuthorizationService);
        this.courseAssignmentService = courseAssignmentService;
        this.channelSubscriptionService = channelSubscriptionService;
    }

    @Override
    protected Set<Integer> getResourceContentGroupIds(GcVideo resource) {
        Integer originCourseId = resource.getOriginCourseId();
        if (originCourseId != null) {
            return courseAssignmentService.getContentGroupIds(originCourseId);
        }
        if (resource.getOriginChannelId() != null) {
            return channelSubscriptionService.getContentGroupIds(resource.getOriginChannelId());
        }

        return Set.of();
    }

    @Override
    public boolean checkAccess(GcVideo resource, PermitAction action, PortalUser portalUser) {
        return getRoleBasedAuthorizationService(resource, portalUser).checkAccess(action);
    }

    @Override
    public void populatePermissions(GcVideo resource, PortalUser portalUser) {
        Map<PermitAction, Boolean> permissions =
            getRoleBasedAuthorizationService(resource, portalUser).getPermissions();

        resource.setPermissions(permissions.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue)));
    }

    @Override
    protected boolean isOwner(GcVideo resource, PortalUser portalUser) {
        if (resource.getOriginChannel() != null) {
            return resource.getOriginChannel().getCreateUserId().equals(portalUser.getUserId());
        }

        if (resource.getOriginCourse() != null) {
            return resource.getOriginCourse().getUserId().equals(portalUser.getUserId());
        }

        return false;
    }

    @Override
    protected boolean checkMemberContentGroupIds(GcVideo resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        Set<Integer> userMemberContentGroupIds = getUserMemberContentGroupIds(portalUser);

        return userMemberContentGroupIds.stream().anyMatch(resourceContentGroupIds::contains);
    }

    @Override
    protected boolean checkManagedContentGroupIds(GcVideo resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        Set<Integer> userManagerContentGroupIds = getUserManagerContentGroupIds(portalUser);

        return userManagerContentGroupIds.stream().anyMatch(resourceContentGroupIds::contains);
    }
}
