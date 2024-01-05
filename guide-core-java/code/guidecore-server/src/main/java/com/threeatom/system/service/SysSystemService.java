//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.entity.SysSystemConfig;
import java.util.List;

public interface SysSystemService extends IService<SysSystem> {
    List<SysSystem> getSystemListByBusinessKeyCache(String key);

    SysSystem getSystemById(Integer id);

    SysSystemConfig getSystemConfig(Integer sysId);

	SysSystem getSystem();
}
