package com.threeatom.common.permissions.service.impl;

import com.threeatom.common.permissions.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("ownerAuthorizationService")
public class OwnerAuthorizationServiceImpl extends RoleBasedAuthorizationService {

    // TODO: review the permissions for the owner
    private static final Map<PermitAction, Boolean> OWNER_PERMISSIONS = Map.of(
        PermitAction.VIEW, true,
        PermitAction.EDIT, true,
        PermitAction.DELETE, true
    );

    @Override
    public Map<PermitAction, Boolean> getPermissions() {
        return OWNER_PERMISSIONS;
    }
}
