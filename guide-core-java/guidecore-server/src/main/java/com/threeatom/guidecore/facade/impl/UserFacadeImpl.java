package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.dto.response.UserPermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimitation;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.UserFacade;
import com.threeatom.guidecore.service.OrgLicenseLimitationService;
import com.threeatom.guidecore.service.UserLicenseService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private static final int PUBLISH_UNLIMITED = -1;

    private final OrgLicenseLimitationService orgLicenseLimitationService;
    private final UserLicenseService userLicenseService;
    private final AuthorizationService authorizationService;

    @Override
    public UserPermissionsDto getPermissions(PortalUser portalUser) {
        Map<String, Boolean> userPortalPermissions = authorizationService.listPortalPermissions(portalUser);

        if (userLicenseService.isLimitedMember(portalUser.getRole())) {
            OrgLicenseLimitation orgLicenseLimitation =
                orgLicenseLimitationService.getByMasterId(portalUser.getMasterId());
            int publishedChannelLimit = orgLicenseLimitation.getPublishedChannelLimit();
            int publishedPlaylistLimit = orgLicenseLimitation.getPublishedPlaylistLimit();

            return userPermissions(publishedChannelLimit, publishedPlaylistLimit, userPortalPermissions);
        }

        return userPermissions(PUBLISH_UNLIMITED, PUBLISH_UNLIMITED, userPortalPermissions);
    }

    private UserPermissionsDto userPermissions(int publishedChannelLimit, int publishedPlaylistLimit,
                                               Map<String, Boolean> userPortalPermissions) {
        return UserPermissionsDto.builder()
            .publishedChannelLimit(publishedChannelLimit)
            .publishedPlaylistLimit(publishedPlaylistLimit)
            .canAccessTeams(userPortalPermissions.get(PortalAction.ACCESS_TEAMS.toString()))
            .build();
    }
}
