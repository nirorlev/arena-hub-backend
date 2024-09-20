package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.service.AuthorizationService;
import com.threeatom.common.permit.service.ResourceAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class SqlAuthorizationServiceImpl implements AuthorizationService {

    private final ResourceAuthorizationService<GcVideo> videoItemAuthorizationService;
    private final ResourceAuthorizationService<PtChannel> channelAuthorizationService;
    private final ResourceAuthorizationService<GcSubject> courseAuthorizationService;
    private final ResourceAuthorizationService<GcAccess> contentGroupAuthorizationService;
    private final ResourceAuthorizationService<GcUserSaveFolder> playlistAuthorizationService;

    @Override
    public boolean checkAccess(GcVideo video, PermitAction action, PortalUser portalUser) {
        return videoItemAuthorizationService.checkAccess(video, action, portalUser);
    }

    @Override
    public boolean checkAccess(GcAccess contentGroup, PermitAction action, PortalUser portalUser) {
        return contentGroupAuthorizationService.checkAccess(contentGroup, action, portalUser);
    }

    @Override
    public boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser) {
        return channelAuthorizationService.checkAccess(channel, action, portalUser);
    }

    @Override
    public boolean checkAccess(GcSubject course, PermitAction action, PortalUser portalUser) {
        return courseAuthorizationService.checkAccess(course, action, portalUser);
    }

    @Override
    public boolean checkAccess(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser) {
        return playlistAuthorizationService.checkAccess(playlist, action, portalUser);
    }

    @Override
    public void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        playlistAuthorizationService.populatePermissions(playlist, portalUser);
    }

    @Override
    public void populatePermissions(PtChannel channel, PortalUser portalUser) {
        channelAuthorizationService.populatePermissions(channel, portalUser);
    }

    @Override
    public void populatePermissions(GcVideo video, PortalUser portalUser) {
        videoItemAuthorizationService.populatePermissions(video, portalUser);
    }

    @Override
    public boolean checkMenuItem(String menuItemKey, PortalUser portalUser) {
        return true;
    }
}
