package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.SysMenu;
import com.threeatom.guidecore.mapper.SysMenuMapper;
import com.threeatom.guidecore.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 * @title: SysMenuServiceImpl
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:16
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Override
    public List<SysMenu> getSysMenuList(Integer masterId) {
        return this.baseMapper.getSysMenuList(masterId);
    }

    @Override
    public List<SysMenu> getLevel3List(Integer masterId) {
        return this.baseMapper.getLevel3List(masterId);
    }

    @Override
    public List<SysMenu> getByMaster(Integer masterId) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<SysMenu>();
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
}
