package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.service.AuthorizationItemService;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationItemServiceImpl implements AuthorizationItemService {

    private final ContentGroupChannelSubscriptionService channelSubscriptionService;
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

    public Set<String> convert(Set<Integer> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.toSet());
    }
}
