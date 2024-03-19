package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.util.I18NUtil;
import com.threeatom.system.entity.SysUser;
import com.threeatom.system.mapper.SysUserMapper;
import com.threeatom.system.service.SysUserService;
import com.threeatom.utils.PasswordSecretUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysUserServiceImpl.class);

    public SysUser getSysUserBySysIdAndUsername(Integer sysId, String username) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("sys_id", sysId);
        queryWrapper.eq("username", username);
        return (SysUser) this.getOne(queryWrapper);
    }

    public SysUser createSysUserByPhoneAndName(
        Integer sysId, String phone, String name, String password) {
        SysUser user = new SysUser();
        user.setSysId(sysId);
        user.setPhone(phone);
        user.setUsername(phone);
        user.setName(name);
        Date now = new Date();
        String salt = PasswordSecretUtil.createSalt();
        user.setSalt(salt);
        user.setCreateTime(now);
        LOGGER.info(salt);
        String pwdHash = (new SimpleHash("MD5", password, salt + "threeatom123$#&")).toHex();
        user.setPassword(pwdHash);
        this.save(user);
        return user;
    }

    @Cacheable(
        value = {"SysUser"},
        key = "'entity:'+#p0")
    public SysUser getSysUserByIdCache(Integer id) {
        LOGGER.info("获取SysUser");
        return (SysUser) this.getById(id);
    }

    public String getTokenByLoginUser(Integer sysId, String username, String password) {
        SysUser user = this.getSysUserBySysIdAndUsername(sysId, username);
        if (user == null) {
            throw new SystemException(I18NUtil.get("user.password.username"));
        }
        String salt = user.getSalt();
        String pwdHash = (new SimpleHash("MD5", password, salt + "threeatom123$#&")).toHex();

        LOGGER.info(pwdHash);
        LOGGER.info(salt);

        if (user.getPassword().equals(pwdHash)) {
            Map<String, String> map = new HashMap<>();
            map.put("sys_id", sysId.toString());
            return JwtUtil.createTokenByUser(user.getId().toString(), map, user.getPassword());
        }

        return "";
    }
}
