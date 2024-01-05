//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.system.entity.SysRole;
import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    List<SysRole> queryRolesByUid(Integer uid);
}
