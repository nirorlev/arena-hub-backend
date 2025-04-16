package com.threeatom.guidecore.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtLoginConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface GcUserService extends IService<GcUser> {

    GcUser getUserByIdCache(Integer id);

    GcUser getUserInfo(Integer id);

    void checkGcUser(String username, String password);

    GcUser getUserByUsername(String username);

    GcUser createGcUser(
        Integer sysId, String username, String password, String firstName, String lastName);

    List<Integer> getTalkerIds(Integer userId, Integer masterId);

    List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds);

    String generateJwtToken(GcUser user, Integer masterId);

    void verifyUsernameNotExists(String userName, String errorMessage);

    Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId);

    void deleteById(Integer id);

    List<GcUser> getTeamUser(Map<String, Object> params, HttpServletRequest request);

    GcUser getCurrentUser(HttpServletRequest request);

    GcUser syncPowtoonUser(String accessToken, PtLoginConfig ptLoginConfig, Integer masterId) throws IOException,
        ClientException;
}
