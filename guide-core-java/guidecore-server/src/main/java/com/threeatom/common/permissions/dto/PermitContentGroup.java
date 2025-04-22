package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.enums.AuthorizationResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitContentGroup extends PermitResource {
    @Override
    public AuthorizationResourceType getType() {
        return AuthorizationResourceType.CONTENT_GROUP;
    }

    @Override
    protected HashMap<String, Object> getFieldAttributes() {
        return new HashMap<>();
    }
}
