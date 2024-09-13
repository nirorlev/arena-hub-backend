package com.threeatom.common.permit.dto;

import com.threeatom.guidecore.constant.ResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitContentGroup extends PermitResource {
    @Override
    public String getType() {
        return ResourceType.contentGroup;
    }

    @Override
    protected HashMap<String, Object> getFieldAttributes() {
        return new HashMap<>();
    }
}
