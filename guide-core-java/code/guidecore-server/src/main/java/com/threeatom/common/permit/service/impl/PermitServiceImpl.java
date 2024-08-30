package com.threeatom.common.permit.service.impl;

import static java.lang.String.format;

import com.threeatom.common.exception.PermitException;
import com.threeatom.common.permit.dto.PermitChannel;
import com.threeatom.common.permit.dto.PermitContentGroup;
import com.threeatom.common.permit.dto.PermitCourse;
import com.threeatom.common.permit.dto.PermitItem;
import com.threeatom.common.permit.dto.PermitPlaylist;
import com.threeatom.common.permit.dto.PermitUser;
import com.threeatom.common.permit.dto.PermitVideoItem;
import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.constant.GroupsType;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserAccessService;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.TenantRead;
import io.permit.sdk.openapi.models.UserRead;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermitServiceImpl implements PermitService {

    private static final String IS_ORG_ADMIN_ATTRIBUTE = "isOrgAdmin";

    private static final String PERMIT_DEV_WIP_ENV_API_KEY =
        "permit_key_fJPWdxjlpLthYKoy8pKs7w9s6GgA1uSJgbo2IwktCYtbN40wz3wMggugaHXkAj6JOt4xp18shjJQrMh1WXVEvA";

    private final PermitConfiguration permitConfiguration;
    private final GcMasterService gcMasterService;
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
    public boolean isUserOrgAdmin(String username) {
        UserRead userRoles;
        try {
            userRoles = readUser(username);

            if (!CollectionUtils.isEmpty(userRoles.attributes)
                && userRoles.attributes.get(IS_ORG_ADMIN_ATTRIBUTE) != null) {
                return (boolean) userRoles.attributes.get(IS_ORG_ADMIN_ATTRIBUTE);
            }
        } catch (Exception e) {
            log.error("Exception when checking if user '{}' is org admin", username, e);
            throw new PermitException(500, format("Error checking if user '%s' is org admin", username), e);
        }

        return false;
    }

    @Override
    public boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId) {
        if (user == null || masterId == null) {
            return false;
        }

        return checkPermit(resource, action, user.getUsername(), masterId);
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

    private PermitPlaylist createPlaylist(GcUserSaveFolder playlist) {
        PermitPlaylist permitPlaylist = new PermitPlaylist();
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
        permitCourse.setId(String.valueOf(course.getId()));
        permitCourse.setPublic(!course.getIsPrivate());
        permitCourse.setPrivate(course.getIsPrivate());
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
        permitChannel.setPrivate(channel.getIsPrivate());
        permitChannel.setContentGroupIds(convert(channelSubscriptionService.getContentGroupIds(channel.getId())));
        permitChannel.setOwnerId(String.valueOf(channel.getCreateUserId()));
        return permitChannel;
    }

    private boolean checkPermit(PermitItem permitItem, String action, PermitUser permitUser) {
        try {
            return permit.check(buildUser(permitUser), action, buildResource(permitItem));
        } catch (IOException | PermitApiError e) {
            log.info("Error checking permission for user '{}', item type '{}' with id '{}' and action '{}'",
                permitUser.getId(), permitItem.getType(), permitItem.getId(), action, e);
            throw new RuntimeException(e);
        }
    }

    private Resource buildResource(PermitItem permitItem) {
        return new Resource.Builder(permitItem.getType())
            .withAttributes(permitItem.getAttributes())
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
                GroupsType.orgMember)));
        permitUser.setManagedContentGroupIds(convert(
            userAccessService.getContentGroupIds(portalUser.getUserId(), portalUser.getMasterId(),
                GroupsType.groupAdmin)));

        return permitUser;
    }

    private PermitVideoItem createVideoItem(GcVideo video) {
        PermitVideoItem permitVideoItem = new PermitVideoItem();

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

    private boolean checkPermit(PermitResource resource, PermitAction action, String username, Integer masterId) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }

        try {
            UserRead permitUser = readUser(username);
            Resource permitResource = getResource(resource, masterId);

            return permit.check(User.fromString(permitUser.key), action.getPermitAction(), permitResource);
        } catch (Exception e) {
            throw new PermitException(
                format("Error checking permission '%s' for resource '%s' and user '%s' from permit", resource, action,
                    username), e);
        }
    }

    private Resource getResource(PermitResource resource, Integer masterId)
        throws PermitContextError, PermitApiError, IOException {
        return new Resource.Builder(resource.getPermitValue())
            .withTenant(readTenant(masterId).key)
            .withAttributes(new HashMap<>())
            .build();
    }

    private TenantRead readTenant(String tenantId) throws PermitContextError, PermitApiError, IOException {
        return permit.api.tenants.get(tenantId);
    }

    private TenantRead readTenant(Integer masterId) throws PermitContextError, PermitApiError, IOException {
        GcMaster gcMaster = gcMasterService.getMasterById(masterId);
        return readTenant(gcMaster.getContext());
    }

    @Override
    public UserRead readUser(String username) throws PermitContextError, PermitApiError, IOException {
        return permit.api.users.get(username);
    }
}
