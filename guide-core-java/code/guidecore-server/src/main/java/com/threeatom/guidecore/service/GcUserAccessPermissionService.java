package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import java.util.List;

public interface GcUserAccessPermissionService extends IService<GcUserAccessPermission> {

    void deleteSubIdAccessPermissionList(Integer masterId, Integer subId);

    void updatePermissionData(Integer masterId, Integer userId);
}
