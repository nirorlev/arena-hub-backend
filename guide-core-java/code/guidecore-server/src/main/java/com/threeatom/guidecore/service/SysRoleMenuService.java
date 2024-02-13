package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.entity.SysRoleMenu;
import java.util.List;

/**
 * @author Administrator
 * @title: SysRoleMenuService
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:36
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    List<SysMenu> getMenuByRoles(List<String> roles);
}
