package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.SysMenu;

import java.util.List;

/**
 * @author Administrator
 * @title: SysMenuService
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:15
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getSysMenuList(Integer masterId);

    List<SysMenu> getLevel3List(Integer masterId);


    List<SysMenu> getByMaster(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);
}
