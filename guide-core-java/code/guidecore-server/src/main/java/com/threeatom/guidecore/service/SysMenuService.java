package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.SysMenu;
import io.permit.sdk.openapi.models.UserRole;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getSysMenuList(Integer masterId, GcUser user);

    List<SysMenu> getLevel3List(Integer masterId, GcUser user);

    List<SysMenu> getByMaster(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);

    List<SysMenu> getSysMenuListByMasterId(Integer masterId, GcUser currentUser);

    List<SysMenu> getLevel3ListByMasterId(Integer masterId, GcUser currentUser);

    List<SysMenu> getMenuByRoles(List<String> roles, GcUser user, Integer masterId);

    List<SysMenu> getSysMenus(GcUser user, PortalUser portalUser, Integer masterId, Integer isGroupAdmin);
}
