package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitContentGroup;
import com.threeatom.common.permissions.dto.PermitCourse;
import com.threeatom.common.permissions.dto.PermitPlaylist;
import com.threeatom.common.permissions.dto.PermitPortal;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.dto.PermitVideoItem;
import com.threeatom.common.permissions.enums.ChannelAction;
import com.threeatom.common.permissions.enums.ContentGroupAction;
import com.threeatom.common.permissions.enums.CourseAction;
import com.threeatom.common.permissions.enums.PlaylistAction;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.enums.VideoItemAction;
import com.threeatom.common.permissions.service.impl.auth.ChannelAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.ContentGroupAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.CourseAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.PlaylistAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.PortalAuthorizationService;
import com.threeatom.common.permissions.service.impl.auth.VideoItemAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class PowtoonAuthorizationService implements AuthorizationService {

    private final AuthorizationItemService authorizationItemService;
    private final ChannelAuthorizationService channelAuthorizationService;
    private final VideoItemAuthorizationService videoItemAuthorizationService;
    private final CourseAuthorizationService courseAuthorizationService;
    private final ContentGroupAuthorizationService contentGroupAuthorizationService;
    private final PlaylistAuthorizationService playlistAuthorizationService;
    private final PortalAuthorizationService portalAuthorizationService;

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

    @Override
    public boolean checkAccess(GcSubject course, PermitAction action, PortalUser portalUser) {
        PermitCourse permitCourse = authorizationItemService.create(course);
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return courseAuthorizationService.checkPermissions(permitUser, CourseAction.valueOf(action.name()),
            permitCourse);
    }

    @Override
    public boolean checkAccess(GcAccess contentGroup, PermitAction action, PortalUser portalUser) {
        PermitContentGroup permitContentGroup = authorizationItemService.create(contentGroup);
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return contentGroupAuthorizationService.checkPermissions(permitUser, ContentGroupAction.valueOf(action.name()),
            permitContentGroup);
    }

    @Override
    public boolean checkAccess(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser) {
        PermitPlaylist permitPlaylist = authorizationItemService.create(playlist);
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return playlistAuthorizationService.checkPermissions(permitUser, PlaylistAction.valueOf(action.name()),
            permitPlaylist);
    }

    @Override
    public boolean checkMenuItem(String menuItemKey, PortalUser portalUser) {
        PortalAction action = authorizationItemService.getPortalActionByMenuItemKey(menuItemKey);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return portalAuthorizationService.checkPermissions(permitUser, action, new PermitPortal());
    }

    @Override
    public void populatePermissions(GcVideo video, PortalUser portalUser) {
        PermitUser permitUser = authorizationItemService.create(portalUser);
        PermitVideoItem permitVideoItem = authorizationItemService.create(video);

        video.setPermissions(
            convertKeysToString(videoItemAuthorizationService.listPermissions(permitUser, permitVideoItem)));
    }

    @Override
    public void populatePermissions(PtChannel channel, PortalUser portalUser) {
        PermitUser permitUser = authorizationItemService.create(portalUser);
        PermitChannel permitChannel = authorizationItemService.create(channel);

        channel.setPermissions(
            convertKeysToString(channelAuthorizationService.listPermissions(permitUser, permitChannel)));
    }

    @Override
    public void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        PermitUser permitUser = authorizationItemService.create(portalUser);
        PermitPlaylist permitPlaylist = authorizationItemService.create(playlist);

        playlist.setPermissions(
            convertKeysToString(playlistAuthorizationService.listPermissions(permitUser, permitPlaylist)));
    }

    private <T> Map<String, Boolean> convertKeysToString(Map<T, Boolean> permissions) {
        return permissions.entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
    }
}
