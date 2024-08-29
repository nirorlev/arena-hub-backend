package com.threeatom.guidecore.service.impl;

import static com.threeatom.common.jwt.JwtUtil.createTokenByUser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.constant.SysConstant;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.mapper.GcUserMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserInfoService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.AuthorizationUtil;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;
import com.threeatom.system.service.SysSystemService;
import com.threeatom.utils.PasswordSecretUtil;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GcUserServiceImpl extends ServiceImpl<GcUserMapper, GcUser> implements GcUserService {


    @Autowired private SysSystemService systemService;
    @Autowired private SysFileService fileService;
    @Autowired private GcUserAccessService userAccessService;
    @Autowired private GcAccessService accessService;
    @Autowired private SysFileService sysFileService;
    @Autowired private GcUserInfoService infoService;

    @Override
    public GcUser getUserInfo(Integer id) {
        GcUser user = this.baseMapper.selectById(id);
        GcUserInfo info = infoService.getById(user.getInfoId());
        if (null != info.getAvatarFileId()) {
            SysFile file = sysFileService.getById(info.getAvatarFileId());
            info.setAvatarFile(file);
        }
        user.setInfo(info);
        return user;
    }

    @Override
    // 中用到，后续需加上，注释原因是因为在各门户首页新进入门户后退出再进入时拿不到code，待研究如何刷新
    public GcUser getUserByIdCache(Integer id) {
        return this.baseMapper.getGcUserByUserId(id);
    }

    @Override
    public List<GcUser> getTalkerByUserIds(
            List<Integer> userIds, HttpServletRequest request, SysSystem sys) {
        List<GcUser> talkerList = this.baseMapper.selectGetTalkerByUserIds(userIds);
        for (GcUser talker : talkerList) {
            sysFileService.getResFullUrl(talker.getInfo().getAvatarFile(), request);
        }
        return talkerList;
    }

    @Override
    public GcUser checkGcUser(String username, String password) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<GcUser>();
        queryWrapper.eq("username", username);
        GcUser user = this.getOne(queryWrapper);
        if (user == null) throw new SystemException(706, I18NUtil.get("guidecore.master.login.noUser"));

        String salt = user.getSalt();
        String pwdHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        if (!pwdHash.equals(user.getPassword()))
            throw new SystemException(101, I18NUtil.get("guidecore.master.login.passError"));

        return user;
    }

    @Override
    @Transactional
    public GcUser createGcUser(
            Integer sysId, String username, String password, String firstName, String lastName) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<GcUser>();
        queryWrapper.eq("sys_id", sysId).eq("username", username);
        int count = this.count(queryWrapper);
        if (count > 0) throw new SystemException(707, I18NUtil.get("user.login"));

        GcUser user = new GcUser();
        GcUserInfo info = new GcUserInfo();
        info.setFirstName(firstName);
        info.setLastName(lastName);
        infoService.saveOrUpdate(info);
        user.setInfo(info);
        user.setInfoId(info.getId());
        user.setSysId(sysId);
        user.setUsername(username);
        String salt = PasswordSecretUtil.createSalt();
        user.setSalt(salt);
        user.setState(1);
        String pwdHash = new SimpleHash("MD5", password, salt + SysConstant.PASS_SALT).toHex();
        user.setPassword(pwdHash);
        this.save(user);
        return user;
    }

    @Override
    public List<Integer> getTalkerIds(Integer userId, Integer masterId) {
        List<GcUserAccess> userAccess =
                userAccessService.selectPtUserAccessByMasterIdAndUserId(userId, masterId);
        log.error("更新时间::" + new Date());
        if (userAccess.size() != 0) {
            return userAccessService.getUserAccessListUserIds(
                    masterId, userAccess.stream().map(GcUserAccess::getId).collect(Collectors.toList()));
        }
        return new ArrayList<>();
    }

    @Override
    public List<GcUser> getUserByUserAccessIds(List<Integer> userAccessIds) {
        return this.baseMapper.getUserByUserAccessIds(userAccessIds);
    }

    @Override
    public Integer getAllUserNums(Integer masterId, Integer type) {
        // 获取用户accessIds type1学生0老师
        QueryWrapper<GcAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_type", type);
        queryWrapper.eq("master_id", masterId);
        List<GcAccess> accessList = accessService.list(queryWrapper);
        List<Integer> accessIds = accessList.stream().map(GcAccess::getId).collect(Collectors.toList());

        // 如果空间刚刚创建，将没有用户。这时返回0
        if (accessIds == null || accessIds.size() < 1) return 0;

        // 获取user数量
        QueryWrapper<GcUserAccess> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("access_id", accessIds);
        List<GcUserAccess> userAccessList = userAccessService.list(queryWrapper1);
        return userAccessList.size();
    }

    @Override
    public String getUserNativeToken(GcUser user, Integer masterId) {
        Map<String, String> params = new HashMap<>();
        params.put("sysId", "2");
        params.put("role", "user");
        params.put("client", "native");
        params.put("masterId", masterId.toString());

        return createTokenByUser(user.getId().toString(), params, user.getPassword());
    }

    @Override
    public GcUser getUserByUserName(String userName) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        return this.getOne(queryWrapper);
    }

    public GcUser getUserByStripeCustomerId(String userName) {
        QueryWrapper<GcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        return this.getOne(queryWrapper);
    }

    @Override
    public Map<Integer, GcUser> getWatchedUserNum(List<Integer> subjectIds, Integer masterId) {
        return this.baseMapper.getWatchedUserNum(subjectIds, masterId);
    }

    @Override
    public int deleteById(Integer id) {
        return this.baseMapper.deleteById(id);
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
