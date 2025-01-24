package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.controller.user.vo.UserCommonInfo;
import com.threeatom.guidecore.entity.GcUserAccess;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface GcUserAccessMapper extends BaseMapper<GcUserAccess> {

    GcUserAccess selectUserAccessByUserAndMaster(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    GcUserAccess selectUserAccessByManagerAndMaster(
        @Param("managerId") Integer managerId, @Param("masterId") Integer masterId);

    List<GcUserAccess> selectUserAccessListByAccessIds(
        @Param("list") List<Integer> accessIds, @Param("info") UserCommonInfo commonInfo);

    List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<GcUserAccess> selectUserAccessListByIds(@Param("ids") List<Integer> ids);

    List<GcUserAccess> selectUserAccessByUser(Integer userId);

    List<Integer> selectGetUserAccessListUserIds(
        @Param("masterId") Integer masterId, @Param("accessIds") List<Integer> accessIds);

    List<Integer> selectGetUserAccessIdListUserIds(
        @Param("masterId") Integer masterId, @Param("accessIds") List<Integer> accessIds);

    List<GcUserAccess> selectUserAccessesByMasterIdAndRole(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("role") String role);

    Integer countUserAccessesByMasterIdAndRole(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId,
        @Param("role") String role);

    Map<String, Long> selectUsersNum(Integer masterId);

    List<Map<String, Object>> getAllUserInThisMaster(
        @Param("masterIds") List<Integer> masterIds,
        @Param("searchFilter") String searchFilter,
        @Param("accessId") Integer accessId);

    List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId);

    List<GcUserAccess> getAccessByUserIdMaster(Integer userId, Integer masterId);

    List<GcUserAccess> getUserAccessListByMasterIdAndUserId(
        @Param("userIdList") List<Integer> userIdList, @Param("masterId") Integer masterId);

    void insertUserAccessList(List<GcUserAccess> list);
}
