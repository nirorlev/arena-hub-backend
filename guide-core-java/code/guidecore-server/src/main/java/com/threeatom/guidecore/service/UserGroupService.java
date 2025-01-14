package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.client.dto.GroupDto;
import com.threeatom.guidecore.entity.UserGroup;
import java.util.List;

public interface UserGroupService extends IService<UserGroup> {
    void syncUserGroups(List<GroupDto> powtoonUserMemberGroups, Integer userId, Integer masterId);
}
