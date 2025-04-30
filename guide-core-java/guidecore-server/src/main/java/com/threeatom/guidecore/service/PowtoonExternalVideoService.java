package com.threeatom.guidecore.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.system.entity.SysFile;

public interface PowtoonExternalVideoService extends IService<PowtoonExternalVideo> {
    PowtoonExternalVideo getBySysFileId(Integer sysFileId);
    List<PowtoonExternalVideo> getByExternalId(String externalId);
    void createExternalVideoForSysFile(SysFile sysFile);
}
