package com.threeatom.system.dll.service.impl;

import com.threeatom.system.dll.service.ISysFileHandler;
import com.threeatom.system.service.SysFileService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSysFileHandler implements ISysFileHandler<SysFileService> {
    @Autowired private SysFileService sysFileService;

    public DefaultSysFileHandler() {}

    public String getUserFolder() {
        return this.getContextName() + "/user";
    }

    public String getSysFolder() {
        return this.getContextName() + "/common";
    }

    public SysFileService getService() {
        return this.sysFileService;
    }

    public String getContextName() {
        return "sys";
    }
}
