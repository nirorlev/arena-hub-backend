package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.client.dto.ManagedGroupDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import java.util.List;

public interface UserManagedGroupService extends IService<UserManagedGroup> {
    List<UserManagedGroup> findUserManagedGroups(PortalUser portalUser);

    void syncUserManagedGroups(List<ManagedGroupDto> powtoonManagedGroups, Integer userId);
}
