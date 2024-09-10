package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.dto.PermitChannel;
import com.threeatom.common.permit.dto.PermitContentGroup;
import com.threeatom.common.permit.dto.PermitCourse;
import com.threeatom.common.permit.dto.PermitResource;
import com.threeatom.common.permit.dto.PermitPlaylist;
import com.threeatom.common.permit.dto.PermitPortal;
import com.threeatom.common.permit.dto.PermitUser;
import com.threeatom.common.permit.dto.PermitVideoItem;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcUserAccessService;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermitServiceImpl implements PermitService {

    private static final String PERMIT_DEV_WIP_ENV_API_KEY =
        "permit_key_fJPWdxjlpLthYKoy8pKs7w9s6GgA1uSJgbo2IwktCYtbN40wz3wMggugaHXkAj6JOt4xp18shjJQrMh1WXVEvA";

    private static final Map<String, String> MENU_ITEM_TO_PERMIT_ACTION = Map.of(
        "Insights", "accessanalytics"
        , "ContentGroups", "accessteams"
    );

    private final PermitConfiguration permitConfiguration;
    private final GcContentGroupCourseAssignmentService courseAssignmentService;
    private final ContentGroupChannelSubscriptionService channelSubscriptionService;
    private final GcUserAccessService userAccessService;

    private Permit permit;

    @PostConstruct
    public void init() {
        permit = new Permit(
            new PermitConfig.Builder(PERMIT_DEV_WIP_ENV_API_KEY)
                .withPdpAddress(permitConfiguration.getPdpAddress())
                .withDebugMode(true)
                .build()
        );
    }

    @Override
    public boolean checkPermit(GcVideo video, String action, PortalUser portalUser) {
        PermitVideoItem permitVideoItem = createVideoItem(video);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitVideoItem, action, permitUser);
    }

    @Override
    public boolean checkPermit(GcAccess contentGroup, String action, PortalUser portalUser) {
        PermitContentGroup permitContentGroup = createContentGroup(contentGroup);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitContentGroup, action, permitUser);
    }

    @Override
    public boolean checkPermit(PtChannel channel, String action, PortalUser portalUser) {
        PermitChannel permitChannel = createChannel(channel);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitChannel, action, permitUser);
    }

    @Override
    public boolean checkPermit(GcSubject course, String action, PortalUser portalUser) {
        PermitCourse permitCourse = createCourse(course);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitCourse, action, permitUser);
    }

    @Override
    public boolean checkPermit(GcUserSaveFolder playlist, String action, PortalUser portalUser) {
        PermitPlaylist permitPlaylist = createPlaylist(playlist);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitPlaylist, action, permitUser);
    }

    @Override
    public void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        playlist.setPermissions(playlistPermissions(playlist, portalUser));
    }

    private Map<String, Boolean> playlistPermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        return Map.of(
            "canShare", checkPermit(playlist, "share", portalUser)
            , "canEdit", checkPermit(playlist, "edit", portalUser)
            , "canDelete", checkPermit(playlist, "delete", portalUser)
            , "canSubscribe", checkPermit(playlist, "subscribe", portalUser)
            , "canUnsubscribe", checkPermit(playlist, "unsubscribe", portalUser)
            , "canManageContent", checkPermit(playlist, "manageContent", portalUser)
        );
    }

    @Override
    public boolean checkMenuItem(String menuItemKey, PortalUser portalUser) {
        if (!MENU_ITEM_TO_PERMIT_ACTION.containsKey(menuItemKey)) {
            return true;
        }

        return checkPermit(new PermitPortal(), MENU_ITEM_TO_PERMIT_ACTION.get(menuItemKey), createUser(portalUser));
    }

    private PermitPlaylist createPlaylist(GcUserSaveFolder playlist) {
        PermitPlaylist permitPlaylist = new PermitPlaylist();
        if (playlist.getId() == null) {
            return permitPlaylist;
        }

        permitPlaylist.setId(String.valueOf(playlist.getId()));
        permitPlaylist.setOwnerId(String.valueOf(playlist.getUserId()));
        permitPlaylist.setPublic(!playlist.getIsPrivate());
        permitPlaylist.setPrivate(playlist.getIsPrivate());

        // playlist does not have content group ids
        permitPlaylist.setContentGroupIds(Set.of());
        return permitPlaylist;
    }

    private PermitCourse createCourse(GcSubject course) {
        PermitCourse permitCourse = new PermitCourse();
        if (course.getId() == null) {
            return permitCourse;
        }

        permitCourse.setId(String.valueOf(course.getId()));
        permitCourse.setPublic(course.isPublic());
        permitCourse.setPrivate(course.isPrivate());
        permitCourse.setContentGroupIds(convert(courseAssignmentService.getContentGroupIds(course.getId())));
        permitCourse.setOwnerId(String.valueOf(course.getUserId()));
        return permitCourse;
    }

    private PermitContentGroup createContentGroup(GcAccess contentGroup) {
        PermitContentGroup permitContentGroup = new PermitContentGroup();
        permitContentGroup.setId(String.valueOf(contentGroup.getId()));
        return permitContentGroup;
    }

    private PermitChannel createChannel(PtChannel channel) {
        PermitChannel permitChannel = new PermitChannel();
        if (channel.getId() == null) {
            return permitChannel;
        }

        permitChannel.setId(channel.getId().toString());
        permitChannel.setPublic(channel.isPublic());
        permitChannel.setPrivate(channel.isPrivate());
        permitChannel.setContentGroupIds(convert(channelSubscriptionService.getContentGroupIds(channel.getId())));
        permitChannel.setOwnerId(String.valueOf(channel.getCreateUserId()));
        return permitChannel;
    }

    private boolean checkPermit(PermitResource permitResource, String action, PermitUser permitUser) {
        try {
            return permit.check(buildUser(permitUser), action, buildResource(permitResource));
        } catch (IOException | PermitApiError e) {
            log.info("Error checking permission for user '{}', item type '{}' with id '{}' and action '{}'",
                permitUser.getId(), permitResource.getType(), permitResource.getId(), action, e);
            throw new RuntimeException(e);
        }
    }

    private Resource buildResource(PermitResource permitResource) {
        return new Resource.Builder(permitResource.getType())
            .withAttributes(permitResource.getAttributes())
            .build();
    }

    private User buildUser(PermitUser permitUser) {
        return new User.Builder(permitUser.getId())
            .withAttributes(permitUser.getAttributes())
            .build();
    }

    private PermitUser createUser(PortalUser portalUser) {
        PermitUser permitUser = new PermitUser();
        permitUser.setId(portalUser.getUserId().toString());
        permitUser.setOrgAdmin(portalUser.isOrgAdmin());

        permitUser.setContentGroupIds(convert(
            userAccessService.getContentGroupIds(portalUser.getUserId(), portalUser.getMasterId(),
                UserGroupRole.GROUP_MEMBER.getRole())));
        permitUser.setManagedContentGroupIds(convert(
            userAccessService.getContentGroupIds(portalUser.getUserId(), portalUser.getMasterId(),
                UserGroupRole.GROUP_ADMIN.getRole())));

        return permitUser;
    }

    private PermitVideoItem createVideoItem(GcVideo video) {
        PermitVideoItem permitVideoItem = new PermitVideoItem();
        if (video.getId() == null) {
            return permitVideoItem;
        }

        permitVideoItem.setId(video.getId().toString());
        permitVideoItem.setOwnerId(String.valueOf(video.getUserId()));
        permitVideoItem.setPublic(video.isPublic());
        permitVideoItem.setPrivate(video.isPrivate());
        permitVideoItem.setContentGroupIds(convert(getVideoContentGroupIds(video)));
        return permitVideoItem;
    }

    private Set<Integer> getVideoContentGroupIds(GcVideo video) {
        Integer originCourseId = video.getOriginCourseId();
        if (originCourseId != null) {
            return courseAssignmentService.getContentGroupIds(originCourseId);
        }

        return channelSubscriptionService.getContentGroupIds(video.getOriginChannelId());
    }

    public Set<String> convert(Set<Integer> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }
}
