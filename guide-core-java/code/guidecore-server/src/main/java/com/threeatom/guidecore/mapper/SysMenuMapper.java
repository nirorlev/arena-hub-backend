package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.SysMenu;
import java.util.List;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getSysMenuList();

    List<SysMenu> getLevel3List();

    List<SysMenu> getSysMenuListByMasterId(Integer masterId);

    List<SysMenu> getLevel3ListByMasterId(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);

    List<SysMenu> getMenuByRoles(List<String> roles);
}
