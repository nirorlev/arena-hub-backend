package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.dto.PermitVideoItem;
import com.threeatom.common.permissions.enums.ChannelAction;
import com.threeatom.common.permissions.enums.VideoItemAction;
import com.threeatom.common.permissions.service.impl.auth.ChannelAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.VideoItemAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class PowtoonAuthorizationService implements AuthorizationService {

    private final AuthorizationItemService authorizationItemService;
    private final ChannelAuthorizationService channelAuthorizationService;
    private final VideoItemAuthorizationService videoItemAuthorizationService;

    @Override
    public boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser) {
        PermitChannel permitChannel = authorizationItemService.create(channel);
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return channelAuthorizationService.checkPermissions(permitUser, ChannelAction.valueOf(action.name()),
            permitChannel);
    }

    @Override
    public boolean checkAccess(GcVideo video, PermitAction action, PortalUser portalUser) {
        PermitVideoItem permitVideo = authorizationItemService.create(video);
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return videoItemAuthorizationService.checkPermissions(permitUser, VideoItemAction.valueOf(action.name()),
            permitVideo);
    }
}
