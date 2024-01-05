//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.system.entity.SysPermission;
import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> queryEntityByUid(Integer uid);
}
