package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PtLoginConfig;

public interface PtLoginConfigService extends IService<PtLoginConfig> {

    PtLoginConfig getByMasterId(Integer masterId);

    PtLoginConfig populatePtLoginConfig(PtLoginConfig ptLoginConfig);
}
