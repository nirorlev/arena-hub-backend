package com.threeatom.common.permit.dto;

import com.threeatom.guidecore.constant.ResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitContentGroup extends PermitItem {
    @Override
    public String getType() {
        return ResourceType.contentGroup;
    }

    @Override
    public HashMap<String, Object> getAttributes() {
        return new HashMap<>();
    }
}
