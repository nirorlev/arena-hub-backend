package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.controller.user.vo.PageParam;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUserAccess;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface GcUserAccessService extends IService<GcUserAccess> {

    boolean createUserAccess(GcUserAccess userAccess);

    GcUserAccess getUserAccessByMasterIdAndUserId(Integer masterId, Integer userId);

    GcUserAccess selectUserAccessByManagerAndMaster(Integer managerId, Integer masterId);

    List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(Integer userId, Integer masterId);

    List<GcUserAccess> getUserAccessListByUserId(Integer userId, HttpServletRequest request);

    void clearCacheAll();

    List<Integer> getUserAccessListUserIds(Integer masterId, List<Integer> accessIds);

    List<Integer> selectGetUserAccessIdListUserIds(Integer masterId, List<Integer> accessIds);

    Integer countUserAccessesByMasterIdAndRole(Integer userId, Integer masterId, String role);

    List<GcUserAccess> getStudentsAccessByTeacherId(
            Integer userId, Integer masterId, Integer page, Integer pageNum);

    List<GcUserAccess> getUsersByAccessIds(List<Integer> accessIds, UserCommonInfo commonInfo);

    Map<String, Long> getMasterIdUsersNum(Integer masterId);

    List<Map<String, Object>> getAllUserInThisMaster(
            List<Integer> masterId,
            String searchFilter,
            HttpServletRequest request,
            PageParam pageParam,
            Integer accessId);

    List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId);

    int deleteById(Integer id);

    GcUserAccess getAccessByUserIdMaster(Integer userId, Integer masterId);

    List<GcUserAccess> getAccessByAccessId(Integer accessId);

    List<GcUserAccess> getUserAccessListByMasterIdAndUserId(
            List<Integer> userIdList, Integer masterId);

    List<GcUserAccess> getAccessListByUser(Integer userId);

    List<GcUserAccess> getAccessListByUserAndMasterId(Integer userId, Integer masterId);

    void insertUserAccessList(List<GcUserAccess> list);

    void deleteUserAccess(Integer userId, Integer masterId, List<Integer> accessId);

    List<GcUserAccess> selectAllUserAccessByAccessId(Integer accessId, Integer masterId);

    Set<Integer> getContentGroupIds(Integer userId, Integer masterId, List<String> roles);

    void syncUserAccessWithPowtoonGroups(
        Integer masterId, List<GcAccess> allPowtoonUserContentGroups, Integer userId, List<Groups> results);

    void removeOutdatedContentGroupAccess(
        List<GcAccess> allContentGroups, List<String> newGroupCodes, Integer userId, Integer masterId);
}
