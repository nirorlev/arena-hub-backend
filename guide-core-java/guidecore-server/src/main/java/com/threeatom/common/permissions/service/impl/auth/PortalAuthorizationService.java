package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitPortal;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.enums.PortalRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class PortalAuthorizationService extends ResourceAuthorizationService<PermitPortal, PortalRole, PortalAction> {

    private static final List<PortalAction> PORTAL_ACTIONS = List.of(
        PortalAction.ACCESS_ANALYTICS,
        PortalAction.ACCESS_TEAMS,
        PortalAction.ACCESS_CONFIG,
        PortalAction.ACCESS_SETTINGS
    );

    private static final Map<PortalRole, Map<PortalAction, Predicate<PermitPortal>>> ROLE_PORTAL_PERMISSIONS = Map.of(
        PortalRole.ADMIN, Map.of(
            PortalAction.ACCESS_ANALYTICS, portal -> true,
            PortalAction.ACCESS_TEAMS, portal -> true,
            PortalAction.ACCESS_CONFIG, portal -> false,
            PortalAction.ACCESS_SETTINGS, portal -> false
        ),
        PortalRole.ORG_ADMIN, Map.of(
            PortalAction.ACCESS_ANALYTICS, portal -> true,
            PortalAction.ACCESS_TEAMS, portal -> true,
            PortalAction.ACCESS_CONFIG, portal -> false,
            PortalAction.ACCESS_SETTINGS, portal -> true
        )
    );

    @Override
    public PortalRole getRole(PermitUser permitUser, PermitPortal permitPortal) {
        if (permitUser.isOrgAdmin()) {
            return PortalRole.ORG_ADMIN;
        }

        if (!permitUser.getManagedContentGroupIds().isEmpty()) {
            return PortalRole.ADMIN;
        }

        return null;
    }

    @Override
    protected Map<PortalAction, Predicate<PermitPortal>> getPermissionMap(PortalRole contentGroupRole) {
        return ROLE_PORTAL_PERMISSIONS.get(contentGroupRole);
    }

    @Override
    protected List<PortalAction> getActions() {
        return PORTAL_ACTIONS;
    }
}
