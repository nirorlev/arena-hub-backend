package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.SysMenu;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getSysMenuList(PortalUser portalUser);

    List<SysMenu> getLevel3List(PortalUser portalUser);

    List<SysMenu> getByMaster(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);

    List<SysMenu> getSysMenuListByMasterId(PortalUser portalUser);

    List<SysMenu> getLevel3ListByMasterId(PortalUser portalUser);

    List<SysMenu> getMenuByRoles(List<String> roles, PortalUser portalUser);

    List<SysMenu> getSysMenus(PortalUser portalUser, Integer isGroupAdmin);
}
