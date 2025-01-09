package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.facade.GroupFacade;
import com.threeatom.guidecore.mapping.ContentGroupMapping;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.UserManagedGroupService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupFacadeImpl implements GroupFacade {

    private final UserManagedGroupService userManagedGroupService;
    private final GcAccessService contentGroupService;
    private final ContentGroupMapping contentGroupMapping;

    @Override
    public List<ContentGroupDto> getUserManagedContentGroups(PortalUser portalUser) {
        List<UserManagedGroup> userManagedGroups = userManagedGroupService.findUserManagedGroups(portalUser);
        List<Integer> userManagedGroupIds = userManagedGroups.stream()
            .map(UserManagedGroup::getGroupId)
            .collect(Collectors.toList());

        List<GcAccess> userManagedContentGroups = contentGroupService.findContentGroupsByGroupIds(userManagedGroupIds);
        return contentGroupMapping.map(userManagedContentGroups);
    }
}
