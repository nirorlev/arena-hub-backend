package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.AuthorisationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCourse extends PermitCollection {

    @Override
    public AuthorisationResourceType getType() {
        return AuthorisationResourceType.COURSE;
    }
}
