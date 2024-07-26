package com.threeatom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getSysUserBySysIdAndUsername(Integer sysId, String username);

    String getTokenByLoginUser(Integer sysId, String username, String password);

    SysUser createSysUserByPhoneAndName(Integer sysId, String phone, String name, String password);

    SysUser getSysUserByIdCache(Integer id);
}
