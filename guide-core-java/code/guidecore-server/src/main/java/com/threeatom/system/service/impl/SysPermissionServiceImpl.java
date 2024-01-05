//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.system.entity.SysPermission;
import com.threeatom.system.mapper.SysPermissionMapper;
import com.threeatom.system.service.SysPermissionService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    public SysPermissionServiceImpl() {
    }

    public List<SysPermission> getPermissionByUid(Integer uid) {
        return ((SysPermissionMapper)this.baseMapper).queryEntityByUid(uid);
    }
}
