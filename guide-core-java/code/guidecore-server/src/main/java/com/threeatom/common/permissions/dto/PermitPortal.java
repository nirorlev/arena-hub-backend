package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.ResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitPortal extends PermitResource {

    @Override
    public String getType() {
        return ResourceType.portal;
    }

    @Override
    protected HashMap<String, Object> getFieldAttributes() {
        return new HashMap<>();
    }
}
