package com.threeatom.common.permissions.service.impl.role;

import com.threeatom.common.permissions.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("orgAdminAuthorizationService")
public class OrgAdminAuthorizationServiceImpl extends RoleBasedAuthorizationService {

    // TODO: review the permissions for the org admin
    private static final Map<PermitAction, Boolean> ORG_ADMIN_PERMISSIONS = Map.of(
        PermitAction.VIEW, true,
        PermitAction.EDIT, true,
        PermitAction.DELETE, true
    );

    @Override
    public Map<PermitAction, Boolean> getPermissions() {
        return ORG_ADMIN_PERMISSIONS;
    }
}
