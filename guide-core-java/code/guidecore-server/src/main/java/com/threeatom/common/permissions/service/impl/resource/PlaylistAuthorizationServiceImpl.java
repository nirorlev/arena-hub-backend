package com.threeatom.common.permissions.service.impl.resource;

import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import com.threeatom.common.permissions.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlaylistAuthorizationServiceImpl extends ResourceAuthorizationService<GcUserSaveFolder> {

    public PlaylistAuthorizationServiceImpl(GcUserAccessService userAccessService,
                                            RoleBasedAuthorizationService ownerAuthorizationService,
                                            RoleBasedAuthorizationService orgAdminAuthorizationService,
                                            RoleBasedAuthorizationService adminAuthorizationService,
                                            RoleBasedAuthorizationService memberAuthorizationService) {
        super(userAccessService, ownerAuthorizationService, orgAdminAuthorizationService, adminAuthorizationService,
            memberAuthorizationService);
    }

    @Override
    protected Set<Integer> getResourceContentGroupIds(GcUserSaveFolder resource) {
        // playlist does not have content group
        return Set.of();
    }

    @Override
    protected boolean isOwner(GcUserSaveFolder resource, PortalUser portalUser) {
        return portalUser.getUserId().equals(resource.getUserId());
    }

    @Override
    protected boolean checkMemberContentGroupIds(GcUserSaveFolder resource, PortalUser portalUser) {
        return false;
    }

    @Override
    protected boolean checkManagedContentGroupIds(GcUserSaveFolder resource, PortalUser portalUser) {
        return false;
    }

    @Override
    public boolean checkAccess(GcUserSaveFolder resource, PermitAction action, PortalUser portalUser) {
        return getRoleBasedAuthorizationService(resource, portalUser).checkAccess(action);
    }

    @Override
    public void populatePermissions(GcUserSaveFolder resource, PortalUser portalUser) {
        resource.setPermissions(
            getRoleBasedAuthorizationService(resource, portalUser).getPermissions().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue)));
    }
}
