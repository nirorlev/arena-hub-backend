package com.threeatom.guidecore.service.impl;

import static com.threeatom.common.jwt.JwtUtil.createTokenByUser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.mapper.GcUserMapper;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.AuthorizationUtil;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.PasswordSecretUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GcUserServiceImpl extends ServiceImpl<GcUserMapper, GcUser> implements GcUserService {

    private final GcUserAccessService userAccessService;
    private final SysFileService sysFileService;
    private final GcUserInfoService infoService;

    @Override
    public GcUser getUserInfo(Integer userId) {
        GcUser user = this.baseMapper.selectById(userId);
        GcUserInfo info = infoService.getById(user.getInfoId());
        user.setInfo(info);

        if (info.getAvatarFileId() != null) {
            SysFile file = sysFileService.getById(info.getAvatarFileId());
            info.setAvatarFile(file);
        }

        return user;
    }

    @Override
    public GcUser getUserByIdCache(Integer id) {
        return this.baseMapper.getGcUserByUserId(id);
    }

    @Override
    public void checkGcUser(String username, String password) {
        GcUser user = getUserByUsername(username);
        String salt = user.getSalt();
        String passwordHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();

        if (!passwordHash.equals(user.getPassword())) {
            throw new SystemException(101, I18NUtil.get("guidecore.master.login.passError"));
        }
    }


    @Override
    public GcUser getUserByUsername(String username) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        GcUser user = this.getOne(queryWrapper);
        if (user == null) {
            throw new SystemException(706, I18NUtil.get("guidecore.master.login.noUser"));
        }

        return user;
    }

    @Override
    @Transactional
    public GcUser createGcUser(
        Integer sysId, String username, String password, String firstName, String lastName) {
        if (countUsers(sysId, username) > 0) {
            throw new SystemException(707, I18NUtil.get("user.login"));
        }

        GcUser user = createNewUser(sysId, username, password, firstName, lastName);
        this.save(user);
        return user;
    }

    private GcUser createNewUser(Integer sysId, String username, String password, String firstName, String lastName) {
        GcUser user = new GcUser();
        GcUserInfo userInfo = infoService.createNew(firstName, lastName);

        user.setInfo(userInfo);
        user.setInfoId(userInfo.getId());
        user.setSysId(sysId);
        user.setUsername(username);

        String salt = PasswordSecretUtil.createSalt();
        user.setSalt(salt);
        user.setState(1);
        String passwordHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        user.setPassword(passwordHash);
        return user;
    }

    private int countUsers(Integer sysId, String username) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sys_id", sysId)
            .eq("username", username);

        return this.count(queryWrapper);
    }

    @Override
    public List<Integer> getTalkerIds(Integer userId, Integer masterId) {
        List<GcUserAccess> userContentGroups =
            userAccessService.selectPtUserAccessByMasterIdAndUserId(userId, masterId);

        if (!userContentGroups.isEmpty()) {
            return userAccessService.getUserAccessListUserIds(masterId, getUserContentGroupIds(userContentGroups));
        }
        return new ArrayList<>();
    }

    private List<Integer> getUserContentGroupIds(List<GcUserAccess> userContentGroups) {
        return userContentGroups.stream()
            .map(GcUserAccess::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds) {
        return this.baseMapper.getUserByUserAccessIds(userAccessIds);
    }

    @Override
    public String generateJwtToken(GcUser user, Integer masterId) {
        Map<String, String> params = new HashMap<>();
        params.put("sysId", "2");
        params.put("role", "user");
        params.put("client", "native");
        params.put("masterId", masterId.toString());

        return createTokenByUser(user.getId().toString(), params, user.getPassword());
    }

    @Override
    public void verifyUsernameNotExists(String userName, String errorMessage) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);

        if (this.count(queryWrapper) > 0) {
            throw new SystemException(errorMessage);
        }
    }

    @Override
    public Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId) {
        return this.baseMapper.getWatchedUserNum(subjectIds, masterId);
    }

    @Override
    public void deleteById(Integer id) {
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<GcUser> getTeamUser(Map<String, Object> params, HttpServletRequest request) {
        return this.baseMapper.getTeamUser(params);
    }

    @Override
    public GcUser getCurrentUser(HttpServletRequest request) {
        if (AuthorizationUtil.isUser(request)) {
            return getUserByIdCache(AuthorizationUtil.getUserUid(request));
        }

        return null;
    }
}
