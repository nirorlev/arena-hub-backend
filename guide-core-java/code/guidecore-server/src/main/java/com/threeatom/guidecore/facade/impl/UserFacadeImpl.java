package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.dto.response.UserPermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimitation;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.UserFacade;
import com.threeatom.guidecore.service.OrgLicenseLimitationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final OrgLicenseLimitationService orgLicenseLimitationService;
    private final AuthorizationService authorizationService;

    @Override
    public UserPermissionsDto getPermissions(PortalUser portalUser) {
        Map<String, Boolean> userPortalPermissions = authorizationService.listPortalPermissions(portalUser);
        OrgLicenseLimitation orgLicenseLimitation = orgLicenseLimitationService.getByMasterId(portalUser.getMasterId());

        return UserPermissionsDto.builder()
            .publishedChannelLimit(orgLicenseLimitation.getPublishedChannelLimit())
            .publishedPlaylistLimit(orgLicenseLimitation.getPublishedPlaylistLimit())
            .canAccessTeams(userPortalPermissions.get(PortalAction.ACCESS_TEAMS.toString()))
            .build();
    }
}
