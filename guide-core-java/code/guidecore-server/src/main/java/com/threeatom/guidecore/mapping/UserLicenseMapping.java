package com.threeatom.guidecore.mapping;


import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimitation;
import org.mapstruct.Mapper;

@Mapper
public interface UserLicenseMapping {

    LicensePermissionsDto map(OrgLicenseLimitation orgLicenseLimitation);
}
