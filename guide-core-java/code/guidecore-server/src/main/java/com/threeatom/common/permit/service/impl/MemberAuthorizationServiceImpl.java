package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("memberAuthorizationService")
public class MemberAuthorizationServiceImpl extends RoleBasedAuthorizationService {
    // TODO: review the permissions for the member
    private static final Map<PermitAction, Boolean> MEMBER_PERMISSIONS = Map.of(
        PermitAction.VIEW, true,
        PermitAction.EDIT, true,
        PermitAction.DELETE, true
    );

    @Override
    public Map<PermitAction, Boolean> getPermissions() {
        return MEMBER_PERMISSIONS;
    }
}
