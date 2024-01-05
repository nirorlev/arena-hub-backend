//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.SysPermission;
import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    List<SysPermission> getPermissionByUid(Integer uid);
}
