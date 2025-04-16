package com.threeatom.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.system.entity.SysUserRole;
import com.threeatom.system.mapper.SysUserRoleMapper;
import com.threeatom.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {
    public SysUserRoleServiceImpl() {}
}
