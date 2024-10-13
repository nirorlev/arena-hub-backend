package com.threeatom.common.permissions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.threeatom.guidecore.constant.AuthorizationResourceType;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitResource {
    private String id;

    @JsonIgnore
    abstract public AuthorizationResourceType getType();

    @JsonIgnore
    public HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("key", id);
        attributes.putAll(getFieldAttributes());
        return attributes;
    }

    @JsonIgnore
    protected abstract HashMap<String, Object> getFieldAttributes();
}
