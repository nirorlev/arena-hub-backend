package com.threeatom.common.permit.dto;

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
    public HashMap<String, Object> getAttributes() {
        return new HashMap<>();
    }
}
