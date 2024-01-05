//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.system.entity.SysBusiness;

public interface SysBusinessService extends IService<SysBusiness> {
    SysBusiness getSysBusinessByKeyCache(String key);
}
