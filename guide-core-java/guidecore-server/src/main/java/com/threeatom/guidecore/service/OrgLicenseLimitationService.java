package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.OrgLicenseLimitation;

public interface OrgLicenseLimitationService extends IService<OrgLicenseLimitation> {

    void save(Integer masterId);

    OrgLicenseLimitation getByMasterId(Integer masterId);
}
