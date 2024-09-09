package com.threeatom.common.permit.dto;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitResource {
    private String id;

    abstract public String getType();

    abstract public HashMap<String, Object> getAttributes();
}
