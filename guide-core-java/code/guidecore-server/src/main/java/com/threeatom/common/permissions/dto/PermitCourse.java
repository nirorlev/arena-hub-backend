package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.AuthorizationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCourse extends PermitCollection {

    @Override
    public AuthorizationResourceType getType() {
        return AuthorizationResourceType.COURSE;
    }
}
