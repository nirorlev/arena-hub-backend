package com.threeatom.guidecore.mapper;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-25
 */
@Component
public interface GcUserAccessPermissionMapper extends BaseMapper<GcUserAccessPermission> {

    int updateGcUserAccessPermissions(@Param("preList") List<GcUserAccessPermission> perList);

    void updateGcUserAccessPermissionsChannel(
            @Param("preList") List<GcUserAccessPermission> permissionList);

    Integer deleteGroupByGroupId(Integer groupId);

    boolean saveUserAccessPermissionList(
            @Param("userAccessPermissionList") List<GcUserAccessPermission> userAccessPermissionList);

    List<GcUserAccessPermission> listContainsSubPremission(Integer masterId, Integer subjectId);

    int updatePermissionsByUserAccessIds(JSONArray subPermission, @Param("ids") List<Integer> ids);

    GcUserAccessPermission getPermissionByUid(Integer userId, Integer masterId);

    List<GcUserAccessPermission> getPermissionByUidList(Integer userId, Integer masterId);

    List<GcUserAccessPermission> getGroupMemberByUidList(Integer userId, Integer masterId);

    List<GcUserAccessPermission> getGroupMemberPermissionByUidList(
            Integer userId, Integer masterId, String roleName);

    Integer getPermissionId(Integer userId, Integer portalId);

    GcUserAccessPermission getPermissionByUserAccessId(Integer userAccessId);

    Integer getUserNumByPortal(@Param("masterId") Integer masterId);

    List<Map<String, Object>> userListContainSub(
            @Param("subId") Integer subId, @Param("masterId") Integer masterId);

    void insertUserPermission(List<GcUserAccessPermission> permissionList);

    void removeUserPermission(List<GcUserAccessPermission> permissionList);

    List<GcUserAccessPermission> selectAllUsersInPortal(@Param("masterId") Integer masterId);

    List<GcUserAccessPermission> getContainsAccessPermissionList(
            @Param("ptId") String ptId, @Param("masterId") Integer masterId);

    List<GcUserAccessPermission> getContainsSubjectAccessPermissionList(@Param("ptId") String ptId);

    void updatePermissionData(@Param("masterId") Integer masterId, @Param("userId") Integer userId);
}
