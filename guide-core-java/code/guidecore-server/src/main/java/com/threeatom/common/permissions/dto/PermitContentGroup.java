package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.AuthorisationResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitContentGroup extends PermitResource {
    @Override
    public AuthorisationResourceType getType() {
        return AuthorisationResourceType.CONTENT_GROUP;
    }

    @Override
    protected HashMap<String, Object> getFieldAttributes() {
        return new HashMap<>();
    }
}
