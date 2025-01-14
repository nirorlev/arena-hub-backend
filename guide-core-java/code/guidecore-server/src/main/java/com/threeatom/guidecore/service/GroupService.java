package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import java.util.List;

public interface GroupService extends IService<Group> {
    void syncGroups(List<Groups> powtoonGroups, Integer userId, Integer masterId);
}
