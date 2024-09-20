package com.threeatom.common.permit.service;

import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ResourceAuthorizationService<T> {

    private final Map<BiPredicate<T, PortalUser>, RoleBasedAuthorizationService> roleBasedAuthorizationServiceMap =
        new HashMap<>();

    private final GcUserAccessService userAccessService;

    private final RoleBasedAuthorizationService ownerAuthorizationService;
    private final RoleBasedAuthorizationService orgAdminAuthorizationService;
    private final RoleBasedAuthorizationService adminAuthorizationService;
    private final RoleBasedAuthorizationService memberAuthorizationService;

    @PostConstruct
    public void init() {
        roleBasedAuthorizationServiceMap.put(this::isOwner, ownerAuthorizationService);
        roleBasedAuthorizationServiceMap.put(this::isOrgAdmin, orgAdminAuthorizationService);
        roleBasedAuthorizationServiceMap.put(this::isAdmin, adminAuthorizationService);
        roleBasedAuthorizationServiceMap.put(this::isMember, memberAuthorizationService);
    }

    protected RoleBasedAuthorizationService getRoleBasedAuthorizationService(T resource, PortalUser portalUser) {
        return roleBasedAuthorizationServiceMap.entrySet().stream()
            .filter(entry -> entry.getKey().test(resource, portalUser))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No RoleBasedAuthorizationService found"));
    }

    protected Set<Integer> getUserMemberContentGroupIds(PortalUser portalUser) {
        return userAccessService.getContentGroupIds(portalUser.getUserId(), portalUser.getMasterId(),
            UserGroupRole.GROUP_MEMBER.getRole());
    }

    protected Set<Integer> getUserManagerContentGroupIds(PortalUser portalUser) {
        return userAccessService.getContentGroupIds(portalUser.getUserId(), portalUser.getMasterId(),
            UserGroupRole.GROUP_ADMIN.getRole());
    }

    protected abstract Set<Integer> getResourceContentGroupIds(T resource);

    protected boolean isOrgAdmin(T resource, PortalUser portalUser) {
        return portalUser.isOrgAdmin();
    }

    protected abstract boolean isOwner(T resource, PortalUser portalUser);

    private boolean isMember(T resource, PortalUser portalUser) {
        if (!portalUser.isMember()) {
            return false;
        }

        return checkMemberContentGroupIds(resource, portalUser);
    }

    protected abstract boolean checkMemberContentGroupIds(T resource, PortalUser portalUser);

    private boolean isAdmin(T resource, PortalUser portalUser) {
        if (!portalUser.isGroupAdmin()) {
            return false;
        }

        return checkManagedContentGroupIds(resource, portalUser);
    }

    protected abstract boolean checkManagedContentGroupIds(T resource, PortalUser portalUser);

    public abstract boolean checkAccess(T resource, PermitAction action, PortalUser portalUser);

    public abstract void populatePermissions(T resource, PortalUser portalUser);
}
