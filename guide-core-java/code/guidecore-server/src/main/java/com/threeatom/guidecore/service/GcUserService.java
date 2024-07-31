package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.system.entity.SysSystem;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcUserService extends IService<GcUser> {

    GcUser getUserByIdCache(Integer id);

    GcUser getUserInfo(Integer id);

    GcUser checkGcUser(String username, String password);

    GcUser createGcUser(
            Integer sysId, String username, String password, String firstName, String lastName);

    List<Integer> getTalkerIds(Integer userId, Integer masterId);

    List<GcUser> getTalkerByUserIds(List<Integer> userIds, HttpServletRequest request, SysSystem sys);

    List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds);

    @Deprecated
    Integer getAllUserNums(Integer masterId, Integer type);

    // 获取app端用户token
    String getUserNativeToken(GcUser user, GcMaster master);

    // 根据用户名查询用户
    GcUser getUserByUserName(String userName);

    Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId);

    int deleteById(Integer id);

    List<GcUser> getTeamUser(Map<String, Object> params, HttpServletRequest request);

    GcUser getCurrentUser(HttpServletRequest request);
}
