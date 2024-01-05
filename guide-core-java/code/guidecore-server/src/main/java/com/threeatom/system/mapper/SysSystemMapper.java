//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.system.entity.SysSystem;
import java.util.List;

public interface SysSystemMapper extends BaseMapper<SysSystem> {
    List<SysSystem> selectSysSystemListByBusinessKey(String businessKey);

    SysSystem selectSystemById(Integer id);
}
