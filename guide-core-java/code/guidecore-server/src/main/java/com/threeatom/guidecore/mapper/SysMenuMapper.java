package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.SysMenu;

import java.util.List;

/**
 * @author Administrator
 * @title: SysmenuMapper
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:17
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getSysMenuList();

    List<SysMenu> getLevel3List();
}
