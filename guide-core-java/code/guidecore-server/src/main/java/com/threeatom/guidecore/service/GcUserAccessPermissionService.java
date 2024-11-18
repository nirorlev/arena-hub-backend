package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import java.util.List;

public interface GcUserAccessPermissionService extends IService<GcUserAccessPermission> {

    GcUserAccessPermission getPermissionByUid(Integer userId, Integer masterId);

    GcUserAccessPermission getPermissionByUserAccessId(Integer userAccessId);

    List<GcUserAccessPermission> getPermissionByUserAccessIdList(List<Integer> ids);

    void insertUserPermission(List<GcUserAccessPermission> permissionList);

    List<GcUserAccessPermission> getPermissionByUidList(Integer userId, Integer masterId);

    List<GcUserAccessPermission> getGroupMemberByUidList(Integer userId, Integer masterId);

    List<GcUserAccessPermission> getGroupMemberPermissionByUidList(
            Integer userId, Integer masterId, String roleName);

    List<GcUserAccessPermission> getContainsSubjectAccessPermissionList(String ptId);

    void deleteSubIdAccessPermissionList(Integer masterId, Integer subId);

    void updatePermissionData(Integer masterId, Integer userId);
}
