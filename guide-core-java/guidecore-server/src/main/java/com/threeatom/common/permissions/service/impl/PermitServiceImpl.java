package com.threeatom.common.permissions.service.impl;

import static com.threeatom.guidecore.constant.PermitAction.ACCESS_ANALYTICS;
import static com.threeatom.guidecore.constant.PermitAction.ACCESS_CONFIG;
import static com.threeatom.guidecore.constant.PermitAction.ACCESS_SETTINGS;
import static com.threeatom.guidecore.constant.PermitAction.ACCESS_TEAMS;
import static com.threeatom.guidecore.constant.PermitAction.COMMENT;
import static com.threeatom.guidecore.constant.PermitAction.DELETE;
import static com.threeatom.guidecore.constant.PermitAction.EDIT;
import static com.threeatom.guidecore.constant.PermitAction.MANAGE_CONTENT;
import static com.threeatom.guidecore.constant.PermitAction.SHARE;
import static com.threeatom.guidecore.constant.PermitAction.SUBSCRIBE;
import static com.threeatom.guidecore.constant.PermitAction.UNSUBSCRIBE;
import static com.threeatom.guidecore.constant.PermitAction.VIEW;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitContentGroup;
import com.threeatom.common.permissions.dto.PermitCourse;
import com.threeatom.common.permissions.dto.PermitPlaylist;
import com.threeatom.common.permissions.dto.PermitPortal;
import com.threeatom.common.permissions.dto.PermitResource;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.dto.PermitVideoItem;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationItemService;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermitServiceImpl implements AuthorizationService {

    private static final List<PermitAction> VIDEO_PERMISSIONS_TO_CHECK = List.of(SHARE, EDIT, DELETE, COMMENT, VIEW);
    private static final List<PermitAction> CHANNEL_PERMISSIONS_TO_CHECK =
        List.of(SHARE, EDIT, DELETE, SUBSCRIBE, UNSUBSCRIBE, MANAGE_CONTENT);
    private static final List<PermitAction> PLAYLIST_PERMISSIONS_TO_CHECK =
        List.of(SHARE, EDIT, DELETE, SUBSCRIBE, UNSUBSCRIBE, MANAGE_CONTENT);
    private static final List<PermitAction> PORTAL_PERMISSIONS_TO_CHECK =
        List.of(
            ACCESS_ANALYTICS, ACCESS_TEAMS,
            ACCESS_SETTINGS, ACCESS_CONFIG);

    private final PermitConfiguration permitConfiguration;
    private final AuthorizationItemService authorizationItemService;

    private Permit permit;

    @PostConstruct
    public void init() {
        permit = new Permit(
            new PermitConfig.Builder(permitConfiguration.getApiKey())
                .withPdpAddress(permitConfiguration.getPdpAddress())
                .withDebugMode(true)
                .build()
        );
    }

    @Override
    public boolean checkAccess(GcVideo video, PermitAction action, PortalUser portalUser) {
        return checkAccess(video, List.of(action), portalUser).get(action.getKey());
    }

    private Map<String, Boolean> checkAccess(GcVideo video, List<PermitAction> actions, PortalUser portalUser) {
        PermitVideoItem permitVideoItem = authorizationItemService.create(video);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return checkAccess(permitVideoItem, actions, permitUser);
    }

    @Override
    public boolean checkAccess(GcAccess contentGroup, PermitAction action, PortalUser portalUser) {
        return checkAccess(contentGroup, List.of(action), portalUser).get(action.getKey());
    }

    private Map<String, Boolean> checkAccess(GcAccess contentGroup, List<PermitAction> actions, PortalUser portalUser) {
        PermitContentGroup permitContentGroup = authorizationItemService.create(contentGroup);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return checkAccess(permitContentGroup, actions, permitUser);
    }

    @Override
    public boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser) {
        return checkAccess(channel, List.of(action), portalUser).get(action.getKey());
    }

    private Map<String, Boolean> checkAccess(PtChannel channel, List<PermitAction> actions, PortalUser portalUser) {
        PermitChannel permitChannel = authorizationItemService.create(channel);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return checkAccess(permitChannel, actions, permitUser);
    }

    @Override
    public boolean checkAccess(GcSubject course, PermitAction action, PortalUser portalUser) {
        return checkAccess(course, List.of(action), portalUser).get(action.getKey());
    }

    private Map<String, Boolean> checkAccess(GcSubject course, List<PermitAction> actions, PortalUser portalUser) {
        PermitCourse permitCourse = authorizationItemService.create(course);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return checkAccess(permitCourse, actions, permitUser);
    }

    @Override
    public boolean checkAccess(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser) {
        return checkAccess(playlist, List.of(action), portalUser).get(action.getKey());
    }

    @Override
    public boolean checkAccess(PortalAction action, PortalUser portalUser) {
        return false;
    }

    private Map<String, Boolean> checkAccess(GcUserSaveFolder playlist, List<PermitAction> actions,
                                             PortalUser portalUser) {
        PermitPlaylist permitPlaylist = authorizationItemService.create(playlist);
        PermitUser permitUser = authorizationItemService.create(portalUser);

        return checkAccess(permitPlaylist, actions, permitUser);
    }

    @Override
    public Map<String, Boolean> listPermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        return playlistPermissions(playlist, portalUser);
    }

    @Override
    public Map<String, Boolean> listPermissions(PtChannel channel, PortalUser portalUser) {
        return channelPermissions(channel, portalUser);
    }

    @Override
    public Map<String, Boolean> listPermissions(GcVideo video, PortalUser portalUser) {
        return videoPermissions(video, portalUser);
    }

    @Override
    public Map<String, Boolean> listPermissions(GcAccess contentGroup, PortalUser portalUser) {
        return Map.of();
    }

    private Map<String, Boolean> videoPermissions(GcVideo video, PortalUser portalUser) {
        return checkAccess(video, VIDEO_PERMISSIONS_TO_CHECK, portalUser);
    }

    private Map<String, Boolean> channelPermissions(PtChannel channel, PortalUser portalUser) {
        return checkAccess(channel, CHANNEL_PERMISSIONS_TO_CHECK, portalUser);
    }

    private Map<String, Boolean> playlistPermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        return checkAccess(playlist, PLAYLIST_PERMISSIONS_TO_CHECK, portalUser);
    }

    @Override
    public Map<String, Boolean> listPortalPermissions(PortalUser portalUser) {
        PermitUser permitUser = authorizationItemService.create(portalUser);
        return checkAccess(new PermitPortal(), PORTAL_PERMISSIONS_TO_CHECK, permitUser);
    }

    @Override
    public Map<String, Boolean> listPermissions(GcSubject course, PortalUser portalUser) {
        return Map.of();
    }

    private Map<String, Boolean> checkAccess(PermitResource permitResource, List<PermitAction> actions,
                                             PermitUser permitUser) {
        User user = buildUser(permitUser);
        Resource resource = buildResource(permitResource);
        Map<String, Boolean> result = new HashMap<>();

        for (PermitAction action : actions) {
            try {
                result.put(action.getKey(), permit.check(user, action.getValue(), resource));
            } catch (IOException | PermitApiError e) {
                log.info("Error checking permission for user '{}', item type '{}' with id '{}' and action '{}'",
                    permitUser.getId(), permitResource.getType(), permitResource.getId(), action, e);
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private Resource buildResource(PermitResource permitResource) {
        return new Resource.Builder(permitResource.getType().getValue())
            .withAttributes(permitResource.getAttributes())
            .build();
    }

    private User buildUser(PermitUser permitUser) {
        return new User.Builder(permitUser.getId())
            .withAttributes(permitUser.getAttributes())
            .build();
    }
}
