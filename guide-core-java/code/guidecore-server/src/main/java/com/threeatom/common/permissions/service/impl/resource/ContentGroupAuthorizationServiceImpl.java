package com.threeatom.common.permissions.service.impl.resource;

import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import com.threeatom.common.permissions.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ContentGroupAuthorizationServiceImpl extends ResourceAuthorizationService<GcAccess> {
    public ContentGroupAuthorizationServiceImpl(GcUserAccessService userAccessService,
                                                RoleBasedAuthorizationService ownerAuthorizationService,
                                                RoleBasedAuthorizationService orgAdminAuthorizationService,
                                                RoleBasedAuthorizationService adminAuthorizationService,
                                                RoleBasedAuthorizationService memberAuthorizationService) {
        super(userAccessService, ownerAuthorizationService, orgAdminAuthorizationService, adminAuthorizationService,
            memberAuthorizationService);
    }

    @Override
    protected Set<Integer> getResourceContentGroupIds(GcAccess resource) {
        return Set.of();
    }

    @Override
    protected boolean isOwner(GcAccess resource, PortalUser portalUser) {
        return false;
    }

    @Override
    protected boolean checkMemberContentGroupIds(GcAccess resource, PortalUser portalUser) {
        Set<Integer> userMemberContentGroupIds = getUserMemberContentGroupIds(portalUser);
        return userMemberContentGroupIds.contains(resource.getId());
    }

    @Override
    protected boolean checkManagedContentGroupIds(GcAccess resource, PortalUser portalUser) {
        Set<Integer> userManagerContentGroupIds = getUserManagerContentGroupIds(portalUser);
        return userManagerContentGroupIds.contains(resource.getId());
    }

    @Override
    public boolean checkAccess(GcAccess resource, PermitAction action, PortalUser portalUser) {
        return getRoleBasedAuthorizationService(resource, portalUser).checkAccess(action);
    }

    @Override
    public void populatePermissions(GcAccess resource, PortalUser portalUser) {
        // not needed for content group
    }
}
