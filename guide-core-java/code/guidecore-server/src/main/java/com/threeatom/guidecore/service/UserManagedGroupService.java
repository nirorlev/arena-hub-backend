package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import java.util.List;

public interface UserManagedGroupService extends IService<UserManagedGroup> {
    List<UserManagedGroup> findUserManagedGroups(PortalUser portalUser);
}
