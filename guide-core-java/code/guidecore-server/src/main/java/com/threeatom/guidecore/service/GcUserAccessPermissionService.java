package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GcUserAccessPermissionService extends IService<GcUserAccessPermission>{
    int updateGcUserAccessPermissions(List<GcUserAccessPermission> perList);

    void updateGcUserAccessPermissionsChannel(List<GcUserAccessPermission> perList);

    GcUserAccessPermission getPermissionByUid(Integer userId , Integer masterId);

    GcUserAccessPermission getPermissionByUserAccessId(Integer userAccessId);

    List<GcUserAccessPermission> selectUserAccessPermissions(List<Integer> userAccessIds);

    List<GcUserAccessPermission> getPermissionByUserAccessIdList(List<Integer> ids);

    void insertUserPermission(List<GcUserAccessPermission> permissionList);

    List<GcUserAccessPermission> getPermissionByUidList(Integer userId , Integer masterId);

    List<GcUserAccessPermission> getGroupMemberByUidList(Integer userId , Integer masterId);

    List<GcUserAccessPermission> getGroupMemberPermissionByUidList(Integer userId , Integer masterId,String roleName);

    List<GcUserAccessPermission> selectAllUsersInPortal(Integer masterId);

    List<GcUserAccessPermission> getContainsAccessPermissionList(String ptId,Integer masterId);

    List<GcUserAccessPermission> getContainsSubjectAccessPermissionList(String ptId);

    void deleteSubIdAccessPermissionList(Integer masterId,Integer subId);

    void updatePermissionData(Integer masterId,Integer userId);
}
