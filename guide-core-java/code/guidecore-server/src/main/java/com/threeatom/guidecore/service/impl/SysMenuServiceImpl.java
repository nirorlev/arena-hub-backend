package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.mapper.SysMenuMapper;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.SysMenuService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService {

    private static final Map<String, PermitResource> PERMIT_CHECK_MENU_RESOURCE_MAPPING =
        Map.of("Insights", PermitResource.PORTAL);
    private static final Map<String, PermitAction> PERMIT_CHECK_MENU_ACTION_MAPPING =
        Map.of("Insights", PermitAction.ACCESS_ANALYTICS);
    private static final Map<String, String> MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING =
        Map.of("Insights", "analyticsEnabled");

    private final PermitService permitService;
    private final FeatureToggleService featureToggleService;

    @Override
    public List<SysMenu> getSysMenuList(Integer masterId, GcUser user) {
        List<SysMenu> sysMenus = this.baseMapper.getSysMenuList();
        return updateMenuItems(sysMenus, user, masterId);
    }

    @Override
    public List<SysMenu> getLevel3List(Integer masterId, GcUser user) {
        List<SysMenu> sysMenus = this.baseMapper.getLevel3List();
        return updateMenuItems(sysMenus, user, masterId);
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
    public List<SysMenu> getSysMenuListByMasterId(Integer masterId, GcUser user) {
        List<SysMenu> sysMenus = this.baseMapper.getSysMenuListByMasterId(masterId);
        return updateMenuItems(sysMenus, user, masterId);
    }

    @Override
    public List<SysMenu> getLevel3ListByMasterId(Integer masterId, GcUser user) {
        List<SysMenu> sysMenus = this.baseMapper.getLevel3ListByMasterId(masterId);
        return updateMenuItems(sysMenus, user, masterId);
    }

    private List<SysMenu> updateMenuItems(List<SysMenu> sysMenus, GcUser user, Integer masterId) {
        if (CollectionUtils.isEmpty(sysMenus)) {
            return sysMenus;
        }

        sysMenus.removeIf(sysMenu -> isFeatureToggleDisabled(masterId, sysMenu)
            || isDisabledOnPermit(user, masterId, sysMenu));

        return sysMenus;
    }

    private boolean isFeatureToggleDisabled(Integer masterId, SysMenu sysMenu) {
        if (!MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING.containsKey(sysMenu.getKey())) {
            return false;
        }

        String featureName = MENU_ITEM_TO_FEATURE_TOGGLE_MAPPING.get(sysMenu.getKey());
        if (masterId != null) {
            return Boolean.parseBoolean(featureToggleService.getFeatureToggle(featureName, masterId).getValue());
        }

        return Boolean.parseBoolean(featureToggleService.getFeatureToggle(featureName).getValue());
    }

    private boolean isDisabledOnPermit(GcUser user, Integer masterId, SysMenu sysMenu) {
        if (PERMIT_CHECK_MENU_RESOURCE_MAPPING.containsKey(sysMenu.getKey())) {
            return !permitService.checkPermit(PERMIT_CHECK_MENU_RESOURCE_MAPPING.get(sysMenu.getKey()),
                PERMIT_CHECK_MENU_ACTION_MAPPING.get(sysMenu.getKey()), user, masterId);
        }

        return false;
    }
}
