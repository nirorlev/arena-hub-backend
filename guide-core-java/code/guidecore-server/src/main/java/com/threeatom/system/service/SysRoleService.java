//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.SysRole;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
    List<SysRole> getUserRolesCache(Integer uid);

    List<SysRole> getUserRoles(Integer uid);

    List<SysRole> getRoles(Integer sysId);
}
