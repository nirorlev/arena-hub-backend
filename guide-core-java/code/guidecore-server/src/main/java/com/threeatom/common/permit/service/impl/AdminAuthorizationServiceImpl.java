package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("adminAuthorizationService")
public class AdminAuthorizationServiceImpl extends RoleBasedAuthorizationService {

    // TODO: review the permissions for the admin
    private static final Map<PermitAction, Boolean> ADMIN_PERMISSIONS = Map.of(
        PermitAction.VIEW, true,
        PermitAction.EDIT, true,
        PermitAction.DELETE, true
    );

    @Override
    public Map<PermitAction, Boolean> getPermissions() {
        return ADMIN_PERMISSIONS;
    }
}
