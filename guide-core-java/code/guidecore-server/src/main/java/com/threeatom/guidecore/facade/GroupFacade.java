package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;

public interface GroupFacade {

    List<ContentGroupDto> getUserManagedContentGroups(PortalUser portalUser);

    GroupResponseDto groups(PortalUser portalUser);
}
