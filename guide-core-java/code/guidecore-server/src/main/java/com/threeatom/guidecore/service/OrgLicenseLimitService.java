package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.OrgLicenseLimit;

public interface OrgLicenseLimitService extends IService<OrgLicenseLimit> {
    OrgLicenseLimit getDefaultLicenseLimit(Integer masterId);
}
