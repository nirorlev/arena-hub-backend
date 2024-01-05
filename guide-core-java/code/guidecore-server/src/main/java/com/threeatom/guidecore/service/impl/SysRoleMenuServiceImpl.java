package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.entity.SysRoleMenu;
import com.threeatom.guidecore.mapper.SysRoleMenuMapper;
import com.threeatom.guidecore.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @title: SysRoleMenuServiceImpl
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:35
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    @Override
    public List<SysMenu> getMenuByRoles(List<String> roles) {
        return this.baseMapper.getMenuByRoles(roles);
    }
}
