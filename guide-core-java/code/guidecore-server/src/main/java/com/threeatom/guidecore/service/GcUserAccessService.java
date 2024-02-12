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
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-25
 */
public interface GcUserAccessService extends GcUserAccessServiceBll {

    /***
     * 创建数据，同时创建副表
     * @param userAccess
     * @return
     */
    boolean createUserAccess(GcUserAccess userAccess);

    GcUserAccess getUserAccessByMasterIdAndUserId(Integer masterId, Integer userId);

    GcUserAccess selectUserAccessByManagerAndMaster(Integer managerId, Integer masterId);

    List<GcUserAccess> selectPtUserAccessByMasterIdAndUserId(Integer userId, Integer masterId);

    List<GcUserAccess> getUserAccessListByUserId(Integer userId, HttpServletRequest request);

    void clearCache(Integer userId, Integer masterId);

    void clearCacheAll();

    List<Integer> getUserAccessListUserIds(Integer masterId, List<Integer> accessIds);

    List<Integer> selectGetUserAccessIdListUserIds(Integer masterId, List<Integer> accessIds);

    Integer selectUserAccessesByMasterId(Integer userId, Integer masterId, String role);

    /***
     * 获取当前空间下教师对应的全部学生表
     * @param userId
     * @param masterId
     * @return
     */
    List<GcUserAccess> getStudentsAccessByTeacherId(
            Integer userId, Integer masterId, Integer page, Integer pageNum);

    /***
     * 获取accessIds下的所有用户
     * @param accessIds
     * @return
     */
    List<GcUserAccess> getUsersByAccessIds(List<Integer> accessIds, UserCommonInfo commonInfo);

    Integer createOrUpdateById(GcUserAccessExt userAccessExt);

    List<Map<String, Object>> getUsersLastLogInDataByMasterIdAndUserIds(
            Integer masterId, List<Integer> userIds, String order);

    /***
     * 获取权限表
     * @param userAccess
     * @return
     */
    GcUserAccessPermission getUserAccessPermission(Integer userAccessId);

    /***
     * 获取影响用户的权限表
     * @param userAccessIds
     * @return
     */
    List<GcUserAccessPermission> getUsersAccessPermissions(List<Integer> userAccessIds);

    /***
     * 批量更新权限表
     * @param perList
     * @return
     */
    int updateUserAccessPermissions(List<GcUserAccessPermission> perList);

    /***
     * 检索教师对应的学生注册数量
     * @param lastDay
     * @param teacherAccessId
     * @return
     */
    Map<String, Integer> getLastUsersNum(List<Integer> lastDays, Integer teacherAccessId);

    /***
     * 检索活跃用户的图表情况
     * @param lastDays
     * @param teacherAccessId
     * @return
     */
    Map<String, Integer> getActiveUsersNum(List<Integer> lastDays, Integer teacherAccessId);

    /***
     * 获取空间下学生和教师的数量
     * @param masterId
     * @return
     */
    Map<String, Long> getMasterIdUsersNum(Integer masterId);

    List<Map<String, Object>> getAllUserInThisMaster(
            List<Integer> masterId,
            String searchFilter,
            HttpServletRequest request,
            PageParam pageParam,
            Integer accessId);

    List<Map<String, Object>> getAllManagerInThisMaster(Integer masterId);

    /***
     * 更新相关用户的权限
     * @param userAccessIds 用户userAccessIds
     * @return
     */
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
}
