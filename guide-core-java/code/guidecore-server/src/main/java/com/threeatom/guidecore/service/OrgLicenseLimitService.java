package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;

public interface OrgLicenseLimitService extends IService<OrgLicenseLimit> {
    OrgLicenseLimit getDefaultLicenseLimit(Integer masterId);

    void checkChannelLimit(Integer orgLicenseId, int expectedChannelCount);

    void checkPlaylistLimit(Integer orgLicenseId, int expectedPlaylistCount);

    LicensePermissionsDto getPermissions(Integer masterId);
}
