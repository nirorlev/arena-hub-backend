package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;

public interface AuthorizationResourceService {
    PermitChannel create(PtChannel channel);

    PermitUser create(PortalUser portalUser);
}
