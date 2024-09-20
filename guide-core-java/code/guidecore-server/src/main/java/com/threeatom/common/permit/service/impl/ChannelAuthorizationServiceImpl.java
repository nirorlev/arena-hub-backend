package com.threeatom.common.permit.service.impl;

import com.threeatom.common.permit.service.ResourceAuthorizationService;
import com.threeatom.common.permit.service.RoleBasedAuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcUserAccessService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ChannelAuthorizationServiceImpl extends ResourceAuthorizationService<PtChannel> {

    private final ContentGroupChannelSubscriptionService channelSubscriptionService;

    public ChannelAuthorizationServiceImpl(
        GcUserAccessService userAccessService,
        RoleBasedAuthorizationService ownerAuthorizationService,
        RoleBasedAuthorizationService orgAdminAuthorizationService,
        RoleBasedAuthorizationService adminAuthorizationService,
        RoleBasedAuthorizationService memberAuthorizationService,
        ContentGroupChannelSubscriptionService channelSubscriptionService) {
        super(userAccessService, ownerAuthorizationService, orgAdminAuthorizationService, adminAuthorizationService,
            memberAuthorizationService);
        this.channelSubscriptionService = channelSubscriptionService;
    }

    @Override
    protected Set<Integer> getResourceContentGroupIds(PtChannel resource) {
        return channelSubscriptionService.getContentGroupIds(resource.getId());
    }

    @Override
    protected boolean isOwner(PtChannel resource, PortalUser portalUser) {
        return portalUser.getUserId().equals(resource.getCreateUserId());
    }

    @Override
    protected boolean checkMemberContentGroupIds(PtChannel resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        return getUserMemberContentGroupIds(portalUser).stream().anyMatch(resourceContentGroupIds::contains);
    }

    @Override
    protected boolean checkManagedContentGroupIds(PtChannel resource, PortalUser portalUser) {
        Set<Integer> resourceContentGroupIds = getResourceContentGroupIds(resource);
        return getUserManagerContentGroupIds(portalUser).stream().anyMatch(resourceContentGroupIds::contains);
    }

    @Override
    public boolean checkAccess(PtChannel resource, PermitAction action, PortalUser portalUser) {
        return getRoleBasedAuthorizationService(resource, portalUser).checkAccess(action);
    }

    @Override
    public void populatePermissions(PtChannel resource, PortalUser portalUser) {
        resource.setPermissions(
            getRoleBasedAuthorizationService(resource, portalUser).getPermissions().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue)));
    }
}
