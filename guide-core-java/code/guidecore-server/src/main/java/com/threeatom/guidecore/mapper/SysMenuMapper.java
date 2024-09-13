package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.SysMenu;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getSysMenuList();

    List<SysMenu> getSysMenuListByMasterId(Integer masterId);

    List<Integer> getParentIdList(Integer masterId);

    List<SysMenu> getChildLevelList(Integer masterId);

    List<SysMenu> getMenuByRoles(@Param("roles") List<String> roles);
}
