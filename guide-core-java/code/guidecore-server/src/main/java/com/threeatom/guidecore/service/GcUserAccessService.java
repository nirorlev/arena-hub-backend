package com.threeatom.guidecore.service;

import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.service.bll.GcUserAccessServiceBll;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface GcUserAccessService extends GcUserAccessServiceBll {

    boolean createUserAccess(GcUserAccess userAccess);

    GcUserAccess getUserAccessByMasterIdAndUserId(Integer masterId, Integer userId);

    GcUserAccess selectUserAccessByManagerAndMaster(Integer managerId, Integer masterId);

    List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(Integer userId, Integer masterId);

    List<GcUserAccess> getUserAccessListByUserId(Integer userId, HttpServletRequest request);

    void clearCache(Integer userId, Integer masterId);

    void clearCacheAll();

    List<Integer> getUserAccessListUserIds(Integer masterId, List<Integer> accessIds);

    List<Integer> selectGetUserAccessIdListUserIds(Integer masterId, List<Integer> accessIds);

    Integer countUserAccessesByMasterIdAndRole(Integer userId, Integer masterId, String role);

    List<GcUserAccess> getStudentsAccessByTeacherId(
            Integer userId, Integer masterId, Integer page, Integer pageNum);

    List<GcUserAccess> getUsersByAccessIds(List<Integer> accessIds, UserCommonInfo commonInfo);

    Integer createOrUpdateById(GcUserAccessExt userAccessExt);

    List<Map<String, Object>> getUsersLastLogInDataByMasterIdAndUserIds(
            Integer masterId, List<Integer> userIds, String order);

    GcUserAccessPermission getUserAccessPermission(Integer userAccessId);

    List<GcUserAccessPermission> getUsersAccessPermissions(List<Integer> userAccessIds);

    int updateUserAccessPermissions(List<GcUserAccessPermission> perList);

    Map<String, Integer> getLastUsersNum(List<Integer> lastDays, Integer teacherAccessId);

    Map<String, Integer> getActiveUsersNum(List<Integer> lastDays, Integer teacherAccessId);

    Map<String, Long> getMasterIdUsersNum(Integer masterId);

    List<Map<String, Object>> getAllUserInThisMaster(
            List<Integer> masterId,
            String searchFilter,
            HttpServletRequest request,
            PageParam pageParam,
            Integer accessId);

    List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId);

    int updateUserAccessPermission(List<Integer> userAccessIds, GcAccess gcAccess);

    Integer saveUserAccessPermission(GcUserAccessPermission userAccessPermission);

    int deleteById(Integer id);

    GcUserAccess getAccessByUserIdMaster(Integer userId, Integer masterId);

    List<GcUserAccess> getAccessByAccessId(Integer accessId);

    GcUserAccess getByUserId(Integer userId);

    List<GcUserAccess> getUserAccessListByMasterIdAndUserId(
            List<Integer> userIdList, Integer masterId);

    List<GcUserAccess> getAccessListByUser(Integer userId);

    List<GcUserAccess> getAccessListByUserAndMasterId(Integer userId, Integer masterId);

    List<Integer> getAccessListBySuperAdmin(Integer userId, Integer masterId);

    void insertUserAccessList(List<GcUserAccess> list);

    void deleteUserAccess(Integer userId, Integer masterId, List<Integer> accessId);

    Integer getGroupAdmin(Integer userId, Integer masterId);

    List<GcUserAccess> selectAllUserAccessByAccessId(Integer accessId, Integer masterId);

    Set<Integer> getContentGroupIds(Integer userId, Integer masterId, String role);
}
