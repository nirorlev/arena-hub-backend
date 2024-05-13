package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.SysMenu;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getSysMenuList(Integer masterId, GcUser user);

    List<SysMenu> getLevel3List(Integer masterId, GcUser user);

    List<SysMenu> getByMaster(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);

    List<SysMenu> getSysMenuListByMasterId(Integer masterId, GcUser currentUser);

    List<SysMenu> getLevel3ListByMasterId(Integer masterId, GcUser currentUser);

    List<SysMenu> getMenuByRoles(List<String> roles);
}
