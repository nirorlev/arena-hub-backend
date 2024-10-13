package com.threeatom.common.permissions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitResource {
    private String id;

    @JsonIgnore
    abstract public String getType();

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
