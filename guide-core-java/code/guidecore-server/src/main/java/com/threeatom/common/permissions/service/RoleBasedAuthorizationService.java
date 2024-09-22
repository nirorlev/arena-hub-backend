package com.threeatom.common.permissions.service;

import com.threeatom.guidecore.constant.PermitAction;
import java.util.Map;

public abstract class RoleBasedAuthorizationService {
    public boolean checkAccess(PermitAction action) {
        return getPermissions().getOrDefault(action, false);
    }

    public abstract Map<PermitAction, Boolean> getPermissions();
}
