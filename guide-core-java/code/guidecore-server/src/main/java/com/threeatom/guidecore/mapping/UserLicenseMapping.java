package com.threeatom.guidecore.mapping;


import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import org.mapstruct.Mapper;

@Mapper
public interface UserLicenseMapping {

    LicensePermissionsDto map(OrgLicenseLimit orgLicenseLimit);
}
