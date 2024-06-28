package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.system.entity.SysFile;

/**
 * @author Cvmcosta
 * @title: PowtoonExternalVideoService
 * @projectName jeeplus
 */
public interface PowtoonExternalVideoService extends IService<PowtoonExternalVideo> {
    public PowtoonExternalVideo getBySysFileId(Integer sysFileId);
    public PowtoonExternalVideo createExternalVideoForSysFile(SysFile sysFile);
}
