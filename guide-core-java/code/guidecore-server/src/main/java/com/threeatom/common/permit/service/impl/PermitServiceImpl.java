package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.dto.PermitChannel;
import com.threeatom.common.permit.dto.PermitContentGroup;
import com.threeatom.common.permit.dto.PermitCourse;
import com.threeatom.common.permit.dto.PermitPlaylist;
import com.threeatom.common.permit.dto.PermitPortal;
import com.threeatom.common.permit.dto.PermitResource;
import com.threeatom.common.permit.dto.PermitUser;
import com.threeatom.common.permit.dto.PermitVideoItem;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.constant.ActionsType;
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
import java.util.HashMap;
import java.util.List;
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
        "Insights", ActionsType.accessanAlytics
        , "ContentGroups", ActionsType.accessTeams
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
        return checkPermit(video, List.of(action), portalUser).get(action);
    }

    private Map<String, Boolean> checkPermit(GcVideo video, List<String> actions, PortalUser portalUser) {
        PermitVideoItem permitVideoItem = createVideoItem(video);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitVideoItem, actions, permitUser);
    }

    @Override
    public boolean checkPermit(GcAccess contentGroup, String action, PortalUser portalUser) {
        return checkPermit(contentGroup, List.of(action), portalUser).get(action);
    }

    private Map<String, Boolean> checkPermit(GcAccess contentGroup, List<String> actions, PortalUser portalUser) {
        PermitContentGroup permitContentGroup = createContentGroup(contentGroup);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitContentGroup, actions, permitUser);
    }

    @Override
    public boolean checkPermit(PtChannel channel, String action, PortalUser portalUser) {
        return checkPermit(channel, List.of(action), portalUser).get(action);
    }

    private Map<String, Boolean> checkPermit(PtChannel channel, List<String> actions, PortalUser portalUser) {
        PermitChannel permitChannel = createChannel(channel);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitChannel, actions, permitUser);
    }

    @Override
    public boolean checkPermit(GcSubject course, String action, PortalUser portalUser) {
        return checkPermit(course, List.of(action), portalUser).get(action);
    }

    private Map<String, Boolean> checkPermit(GcSubject course, List<String> actions, PortalUser portalUser) {
        PermitCourse permitCourse = createCourse(course);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitCourse, actions, permitUser);
    }

    @Override
    public boolean checkPermit(GcUserSaveFolder playlist, String action, PortalUser portalUser) {
        return checkPermit(playlist, List.of(action), portalUser).get(action);
    }

    private Map<String, Boolean> checkPermit(GcUserSaveFolder playlist, List<String> actions, PortalUser portalUser) {
        PermitPlaylist permitPlaylist = createPlaylist(playlist);
        PermitUser permitUser = createUser(portalUser);

        return checkPermit(permitPlaylist, actions, permitUser);
    }

    @Override
    public void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        playlist.setPermissions(playlistPermissions(playlist, portalUser));
    }

    @Override
    public void populatePermissions(PtChannel channel, PortalUser portalUser) {
        channel.setPermissions(channelPermissions(channel, portalUser));
    }

    @Override
    public void populatePermissions(GcVideo video, PortalUser portalUser) {
        Map<String, Boolean> permissions = videoPermissions(video, portalUser);

        video.setPermissions(permissions);
        video.getVideoFile().setPermissions(permissions);
    }

    private Map<String, Boolean> videoPermissions(GcVideo video, PortalUser portalUser) {
        List<String> permissionsToCheck = List.of(ActionsType.share, ActionsType.edit, ActionsType.delete, ActionsType.comment, ActionsType.view);
        return checkPermit(video, permissionsToCheck, portalUser);
    }

    private Map<String, Boolean> channelPermissions(PtChannel channel, PortalUser portalUser) {
        List<String> permissionsToCheck =
            List.of(ActionsType.share, ActionsType.edit, ActionsType.delete, ActionsType.subscribe, ActionsType.manageContent);
        return checkPermit(channel, permissionsToCheck, portalUser);
    }

    private Map<String, Boolean> playlistPermissions(GcUserSaveFolder playlist, PortalUser portalUser) {
        List<String> permissionsToCheck =
            List.of(ActionsType.share, ActionsType.edit, ActionsType.delete, ActionsType.subscribe, ActionsType.manageContent);
        return checkPermit(playlist, permissionsToCheck, portalUser);
    }

    @Override
    public boolean checkMenuItem(String menuItemKey, PortalUser portalUser) {
        if (!MENU_ITEM_TO_PERMIT_ACTION.containsKey(menuItemKey)) {
            return true;
        }

        String action = MENU_ITEM_TO_PERMIT_ACTION.get(menuItemKey);
        return checkPermit(new PermitPortal(), List.of(action), createUser(portalUser)).get(action);
    }

    private PermitPlaylist createPlaylist(GcUserSaveFolder playlist) {
        PermitPlaylist permitPlaylist = new PermitPlaylist();
        permitPlaylist.setOwnerId(String.valueOf(playlist.getUserId()));

        if (playlist.getId() == null) {
            return permitPlaylist;
        }

        permitPlaylist.setId(String.valueOf(playlist.getId()));
        permitPlaylist.setPublic(!playlist.getIsPrivate());
        permitPlaylist.setPrivate(playlist.getIsPrivate());

        // playlist does not have content group ids
        permitPlaylist.setContentGroupIds(Set.of());
        return permitPlaylist;
    }

    private PermitCourse createCourse(GcSubject course) {
        PermitCourse permitCourse = new PermitCourse();
        permitCourse.setOwnerId(String.valueOf(course.getUserId()));
        if (course.getId() == null) {
            return permitCourse;
        }

        permitCourse.setId(String.valueOf(course.getId()));
        permitCourse.setPublic(course.isPublic());
        permitCourse.setPrivate(course.isPrivate());
        permitCourse.setContentGroupIds(convert(courseAssignmentService.getContentGroupIds(course.getId())));
        return permitCourse;
    }

    private PermitContentGroup createContentGroup(GcAccess contentGroup) {
        PermitContentGroup permitContentGroup = new PermitContentGroup();
        permitContentGroup.setId(String.valueOf(contentGroup.getId()));
        return permitContentGroup;
    }

    private PermitChannel createChannel(PtChannel channel) {
        PermitChannel permitChannel = new PermitChannel();
        permitChannel.setOwnerId(String.valueOf(channel.getCreateUserId()));
        if (channel.getId() == null) {
            return permitChannel;
        }

        permitChannel.setId(channel.getId().toString());
        permitChannel.setPublic(channel.isPublic());
        permitChannel.setPrivate(channel.isPrivate());
        permitChannel.setContentGroupIds(convert(channelSubscriptionService.getContentGroupIds(channel.getId())));
        return permitChannel;
    }

    private Map<String, Boolean> checkPermit(PermitResource permitResource, List<String> actions,
                                             PermitUser permitUser) {
        User user = buildUser(permitUser);
        Resource resource = buildResource(permitResource);
        Map<String, Boolean> result = new HashMap<>();

        for (String action : actions) {
            try {
                result.put(action, permit.check(user, action, resource));
            } catch (IOException | PermitApiError e) {
                log.info("Error checking permission for user '{}', item type '{}' with id '{}' and action '{}'",
                    permitUser.getId(), permitResource.getType(), permitResource.getId(), action, e);
                throw new RuntimeException(e);
            }
        }

        return result;
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
        permitVideoItem.setOwnerId(String.valueOf(video.getUserId()));
        if (video.getId() == null) {
            return permitVideoItem;
        }

        permitVideoItem.setId(video.getId().toString());
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
