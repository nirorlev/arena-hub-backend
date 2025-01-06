package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.UserGroup;
import java.util.List;

public interface UserGroupService extends IService<UserGroup> {
    void syncUserGroups(List<Groups> groups, List<Group> arenaGroups, Integer userId);
}
