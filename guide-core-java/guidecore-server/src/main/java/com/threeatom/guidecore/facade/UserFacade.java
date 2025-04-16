package com.threeatom.guidecore.facade;


import com.threeatom.guidecore.dto.response.UserPermissionsDto;
import com.threeatom.guidecore.entity.PortalUser;

public interface UserFacade {

    UserPermissionsDto getPermissions(PortalUser portalUser);
}
