//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.config;

import com.threeatom.system.entity.SysBusiness;
import com.threeatom.system.service.SysBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public abstract class SystemConfig {
    @Autowired
    private SysBusinessService businessService;

    public SystemConfig() {
    }

    public abstract String getBusinessKey();

    @Bean(
        name = {"currentBusiness"}
    )
    public SysBusiness getCurrentBusiness() {
        String key = this.getBusinessKey();
        return this.businessService.getSysBusinessByKeyCache(key);
    }
}
