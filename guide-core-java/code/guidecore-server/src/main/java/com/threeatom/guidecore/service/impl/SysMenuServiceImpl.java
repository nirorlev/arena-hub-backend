package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.GroupsType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.enums.UserOrgRole;
import com.threeatom.guidecore.mapper.SysMenuMapper;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.SysMenuService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService {

    private static final Map<String, String> MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING =
        Map.of(
            "Insights", "analyticsEnabled",
            "admin-course", "coursesEnabled",
            "Home", "homepageMenuEnabled"
        );

    private final AuthorizationService authorizationService;
    private final FeatureToggleService featureToggleService;
    private final GcUserAccessService userAccessService;

    @Override
    public List<SysMenu> getSysMenuList(PortalUser portalUser) {
        List<SysMenu> sysMenus = this.baseMapper.getSysMenuList();
        return updateMenuItems(sysMenus, portalUser);
    }

    @Override
    public List<SysMenu> getLevel3List(PortalUser portalUser) {
        List<SysMenu> sysMenus = this.baseMapper.getLevel3List();
        if (portalUser == null) {
            return sysMenus;
        }

        return updateMenuItems(sysMenus, portalUser);
    }

    @Override
    public List<SysMenu> getByMaster(Integer masterId) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Integer> getParentIdList(Integer masterId) {
        return this.baseMapper.getParentIdList(masterId);
    }

    @Override
    public List<SysMenu> getChildLevelList(Integer masterId) {
        return this.baseMapper.getChildLevelList(masterId);
    }

    @Override
    public List<SysMenu> getSysMenuListByMasterId(PortalUser portalUser) {
        List<SysMenu> sysMenus = this.baseMapper.getSysMenuListByMasterId(portalUser.getMasterId());
        return updateMenuItems(sysMenus, portalUser);
    }

    @Override
    public List<SysMenu> getLevel3ListByMasterId(PortalUser portalUser) {
        List<SysMenu> sysMenus = this.baseMapper.getLevel3ListByMasterId(portalUser.getMasterId());
        return updateMenuItems(sysMenus, portalUser);
    }

    @Override
    public List<SysMenu> getMenuByRoles(List<String> roles, PortalUser portalUser) {
        List<SysMenu> menuByRoles = this.baseMapper.getMenuByRoles(roles);
        return updateMenuItems(menuByRoles, portalUser);
    }

    private List<SysMenu> updateMenuItems(List<SysMenu> sysMenus, PortalUser portalUser) {
        if (CollectionUtils.isEmpty(sysMenus)) {
            return sysMenus;
        }

        sysMenus.removeIf(sysMenu -> {
            if (isFeatureToggleDisabled(portalUser.getMasterId(), sysMenu)) {
                return true;
            }
            return !authorizationService.checkMenuItem(sysMenu.getKey(), portalUser);
        });

        return sysMenus;
    }

    private boolean isFeatureToggleDisabled(Integer masterId, SysMenu sysMenu) {
        if (!MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING.containsKey(sysMenu.getKey())) {
            return false;
        }

        String featureName = MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING.get(sysMenu.getKey());
        return !Boolean.parseBoolean(featureToggleService.getFeatureToggle(featureName, masterId).getValue());
    }

    @Override
    public List<SysMenu> getSysMenus(PortalUser portalUser) {
        List<String> roles = getRoles(portalUser);
        List<SysMenu> roleMenus = new ArrayList<>();
        if (!roles.isEmpty()) {
            roleMenus.addAll(getMenuByRoles(roles, portalUser));
        }

        if (portalUser.isOrgAdmin() || portalUser.isGroupAdmin()) {
            roleMenus.add(getNewGroupAdminSysMenu());
        }

        return roleMenus;
    }

    private SysMenu getNewGroupAdminSysMenu() {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setName("courses-groupAdmin");
        sysMenu.setKey("courses-groupAdmin");
        sysMenu.setState(TableConstant.COMMON_ZERO);
        sysMenu.setLevel(1);
        sysMenu.setRemarks("groupAdmin");
        return sysMenu;
    }

    private List<String> getRoles(PortalUser portalUser) {
        List<String> roles = new ArrayList<>();
        UserOrgRole role = portalUser.getRole();
        List<Integer> gcUserAccessList = userAccessService.getAccessListBySuperAdmin(
            portalUser.getUserId(), portalUser.getMasterId());

        if (CollectionUtils.isNotEmpty(gcUserAccessList)) {
            roles.add(GroupsType.superAdmin);
        }
        if (role.isMember()) {
            roles.add(UserOrgRole.MEMBER.getRole());
        }
        if (role.isAdmin()) {
            roles.add(UserOrgRole.ADMIN.getRole());
        }
        if (portalUser.isOrgAdmin()) {
            roles.add(UserOrgRole.ORG_ADMIN.getRole());
        }

        return roles;
    }
}
