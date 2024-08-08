package com.threeatom.guidecore.mapping;


import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.entity.UserLicense;
import org.mapstruct.Mapper;

@Mapper
public interface UserLicenseMapping {

    LicenseUsageDto map(UserLicense userLicense);

    LicensePermissionsDto map(OrgLicenseLimit orgLicenseLimit);
}
