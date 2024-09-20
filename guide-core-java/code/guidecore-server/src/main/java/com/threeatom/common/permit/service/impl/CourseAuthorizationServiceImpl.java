package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.service.ResourceAuthorizationService;
import com.threeatom.common.permit.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CourseAuthorizationServiceImpl extends ResourceAuthorizationService<GcSubject> {

    private final GcContentGroupCourseAssignmentService courseAssignmentService;

    public CourseAuthorizationServiceImpl(GcUserAccessService userAccessService,
                                          RoleBasedAuthorizationService ownerAuthorizationService,
                                          RoleBasedAuthorizationService orgAdminAuthorizationService,
                                          RoleBasedAuthorizationService adminAuthorizationService,
                                          RoleBasedAuthorizationService memberAuthorizationService,
                                          GcContentGroupCourseAssignmentService courseAssignmentService) {
        super(userAccessService, ownerAuthorizationService, orgAdminAuthorizationService, adminAuthorizationService,
            memberAuthorizationService);
        this.courseAssignmentService = courseAssignmentService;
    }

    @Override
    protected Set<Integer> getResourceContentGroupIds(GcSubject resource) {
        return courseAssignmentService.getContentGroupIds(resource.getId());
    }

    @Override
    protected boolean isOwner(GcSubject resource, PortalUser portalUser) {
        return portalUser.getUserId().equals(resource.getUserId());
    }

    @Override
    protected boolean checkMemberContentGroupIds(GcSubject resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        return getUserMemberContentGroupIds(portalUser).stream().anyMatch(resourceContentGroupIds::contains);
    }

    @Override
    protected boolean checkManagedContentGroupIds(GcSubject resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        return getUserManagerContentGroupIds(portalUser).stream().anyMatch(resourceContentGroupIds::contains);
    }

    @Override
    public boolean checkAccess(GcSubject resource, PermitAction action, PortalUser portalUser) {
        return getRoleBasedAuthorizationService(resource, portalUser).checkAccess(action);
    }

    @Override
    public void populatePermissions(GcSubject resource, PortalUser portalUser) {
        resource.setPermissions(
            getRoleBasedAuthorizationService(resource, portalUser).getPermissions().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue)));
    }
}
