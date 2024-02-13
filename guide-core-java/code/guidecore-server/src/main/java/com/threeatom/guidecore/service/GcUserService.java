package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.system.entity.SysSystem;
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
public interface GcUserService extends IService<GcUser> {

    /***
     * 	通过id获取user 缓存版本
     * @param id
     * @return
     */
    GcUser getUserByIdCache(Integer id);

    GcUser getUserInfo(Integer id);

    /***
     * 登录检查
     * @param username
     * @param password
     * @return
     */
    GcUser checkGcUser(String username, String password);

    GcUser createGcUser(
            Integer sysId, String username, String password, String firstName, String lastName);

    List<Integer> getTalkerIds(Integer userId, Integer masterId);

    List<GcUser> getTalkerByUserIds(List<Integer> userIds, HttpServletRequest request, SysSystem sys);

    List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds);

    @Deprecated
    Integer getAllUserNums(Integer masterId, Integer type);

    // 获取app端用户token
    String getUserNativeToken(GcUser user);

    // 根据用户名查询用户
    GcUser getUserByUserName(String userName);

    Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId);

    int deleteById(Integer id);

    List<GcUser> getTeamUser(Map<String, Object> params, HttpServletRequest request);
}
