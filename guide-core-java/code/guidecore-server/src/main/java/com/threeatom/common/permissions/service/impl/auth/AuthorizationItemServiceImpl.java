package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitContentGroup;
import com.threeatom.common.permissions.dto.PermitCourse;
import com.threeatom.common.permissions.dto.PermitPlaylist;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.dto.PermitVideoItem;
import com.threeatom.common.permissions.enums.PlaylistAction;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationItemService;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationItemServiceImpl implements AuthorizationItemService {

    private static final Map<String, PortalAction> MENU_ITEM_TO_PORTAL_ACTION = Map.of(
        "Insights", PortalAction.ACCESS_ANALYTICS
        , "ContentGroups", PortalAction.ACCESS_TEAMS
    );

    private final ContentGroupChannelSubscriptionService channelSubscriptionService;
    private final GcContentGroupCourseAssignmentService courseAssignmentService;
    private final GcUserAccessService userAccessService;

    public PermitChannel create(PtChannel channel) {
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

    @Override
    public PermitUser create(PortalUser portalUser) {
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

    @Override
    public PermitVideoItem create(GcVideo video) {
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

    @Override
    public PermitPlaylist create(GcUserSaveFolder playlist) {
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

    @Override
    public PermitCourse create(GcSubject course) {
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

    @Override
    public PermitContentGroup create(GcAccess contentGroup) {
        PermitContentGroup permitContentGroup = new PermitContentGroup();
        permitContentGroup.setId(String.valueOf(contentGroup.getId()));
        return permitContentGroup;
    }

    @Override
    public PortalAction getPortalActionByMenuItemKey(String menuItemKey) {
        return MENU_ITEM_TO_PORTAL_ACTION.get(menuItemKey);
    }

    private Set<Integer> getVideoContentGroupIds(GcVideo video) {
        Integer originCourseId = video.getOriginCourseId();
        if (originCourseId != null) {
            return courseAssignmentService.getContentGroupIds(originCourseId);
        }

        return channelSubscriptionService.getContentGroupIds(video.getOriginChannelId());
    }

    private Set<String> convert(Set<Integer> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }
}
