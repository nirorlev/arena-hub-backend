package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface OrgLicenseLimitService extends IService<OrgLicenseLimit> {

    void save(Integer masterId);

    LicensePermissionsDto getPermissions(Integer masterId);

    OrgLicenseLimit getByMasterId(Integer masterId);
}
