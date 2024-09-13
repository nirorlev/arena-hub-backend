package com.threeatom.common.permit.dto;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitResource {
    private String id;

    abstract public String getType();

    public HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("key", id);
        attributes.putAll(getFieldAttributes());
        return attributes;
    }

    protected abstract HashMap<String, Object> getFieldAttributes();
}
