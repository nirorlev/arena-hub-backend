package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public List<SysMenu> getSysMenuList() {
        return this.baseMapper.getSysMenuList();
    }

    @Override
    public List<SysMenu> getLevel3List() {
        return this.baseMapper.getLevel3List();
    }
}
