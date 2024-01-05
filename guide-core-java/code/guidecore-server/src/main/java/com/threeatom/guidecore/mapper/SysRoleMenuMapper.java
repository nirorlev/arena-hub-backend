package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @title: SysRoleMenuMapper
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:33
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    List<SysMenu> getMenuByRoles(@Param("roles") List<String> roles);
}
