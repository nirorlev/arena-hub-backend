package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface GcUserAccessMapper extends BaseMapper<GcUserAccess> {

    GcUserAccess selectUserAccessByUserAndMaster(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    GcUserAccess selectUserAccessByManagerAndMaster(
            @Param("managerId") Integer managerId, @Param("masterId") Integer masterId);

    GcUserAccess selectUserAccessByUserAndMasterContext(
            @Param("userId") Integer userId, @Param("context") String context);

    List<GcUserAccess> selectUserAccessListByAccessIds(
            @Param("list") List<Integer> accessIds, @Param("info") UserCommonInfo commonInfo);

    List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<GcUserAccess> selectUserAccessListByIds(@Param("ids") List<Integer> ids);

    List<GcUserAccess> selectUserAccessByUser(Integer userId);

    List<Integer> getAccessListBySuperAdmin(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<Integer> selectGetUserAccessListUserIds(
            @Param("masterId") Integer masterId, @Param("accessIds") List<Integer> accessIds);

    List<Integer> selectGetUserAccessIdListUserIds(
            @Param("masterId") Integer masterId, @Param("accessIds") List<Integer> accessIds);

    List<Integer> getIdsListAccessIds(
            @Param("accessIds") List<Integer> ids, @Param("masterId") Integer masterId);

    List<GcUserAccess> selectUserAccessesByMasterIdAndRole(
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId,
            @Param("role") String role);

    Integer countUserAccessesByMasterIdAndRole(
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId,
            @Param("role") String role);

    /***
     * 获取当前空间用户集合的最后一次登录时间
     * @param masterId
     * @param userIds
     * @return
     */
    List<Map<String, Object>> selectUserAccessExtListByMasterIdAndUserIds(
            @Param("masterId") Integer masterId,
            @Param("userIds") List<Integer> userIds,
            @Param("order") String order);

    int insertGcUserAccessExt(GcUserAccessExt userAccessExt);

    int updateGcUserAccessExtById(GcUserAccessExt userAccessExt);

    GcUserAccessExt selectGcUserAccessExtByAccessId(Integer accessId);

    Map<String, Integer> selectLastUsersNum(
            @Param("lastDays") List<Integer> lastDays, @Param("teacherAccessId") Integer teacherAccessId);

    Map<String, Integer> selectActiveUsersNum(
            @Param("lastDays") List<Integer> lastDays, @Param("teacherAccessId") Integer teacherAccessId);

    Map<String, Long> selectUsersNum(Integer masterId);

    List<Map<String, Object>> getAllUserInThisMaster(
            @Param("masterIds") List<Integer> masterIds,
            @Param("searchFilter") String searchFilter,
            @Param("accessId") Integer accessId);

    List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId);

    GcUserAccess getAccessByUserIdMaster(Integer userId, Integer masterId);

    List<GcUserAccess> getUserAccessListByMasterIdAndUserId(
            @Param("userIdList") List<Integer> userIdList, @Param("masterId") Integer masterId);

    List<GcUserAccess> countUsersInPortal();

    void insertUserAccessList(List<GcUserAccess> list);
}
