package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.entity.UserLicense;

public interface OrgLicenseLimitService extends IService<OrgLicenseLimit> {
    OrgLicenseLimit getDefaultLicenseLimit(Integer masterId);

    void checkChannelLimit(UserLicense dbUserLicense, int expectedChannelCount);

    void checkPlaylistLimit(UserLicense dbUserLicense, int expectedPlaylistCount);

    LicensePermissionsDto getPermissions(Integer masterId);
}
