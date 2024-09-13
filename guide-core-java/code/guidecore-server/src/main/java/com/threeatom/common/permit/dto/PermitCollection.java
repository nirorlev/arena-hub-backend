package com.threeatom.common.permit.dto;

import java.util.HashMap;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitCollection extends PermitResource {
    private String ownerId;
    private boolean isPublic;
    private boolean isPrivate;
    private Set<String> contentGroupIds;

    @Override
    protected HashMap<String, Object> getFieldAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ownerId", ownerId);
        attributes.put("isPublic", isPublic);
        attributes.put("isPrivate", isPrivate);
        attributes.put("contentGroupIds", contentGroupIds);
        return attributes;
    }
}
