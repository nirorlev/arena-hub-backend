package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.ChannelAction;
import com.threeatom.common.permissions.service.impl.auth.ChannelAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class PowtoonAuthorizationService implements AuthorizationService {

    private final AuthorizationResourceService authorizationResourceService;
    private final ChannelAuthorizationService channelAuthorizationService;

    public boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser) {
        PermitChannel permitChannel = authorizationResourceService.create(channel);
        PermitUser permitUser = authorizationResourceService.create(portalUser);
        return channelAuthorizationService.checkPermissions(permitUser, ChannelAction.valueOf(action.name()),
            permitChannel);
    }
}
