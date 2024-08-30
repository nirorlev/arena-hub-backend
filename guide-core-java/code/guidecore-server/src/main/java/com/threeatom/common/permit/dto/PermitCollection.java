package com.threeatom.common.permit.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitCollection extends PermitItem {
    private String ownerId;
    private boolean isPublic;
    private boolean isPrivate;
    private List<String> contentGroupIds;

    @Override
    public HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ownerId", ownerId);
        attributes.put("isPublic", isPublic);
        attributes.put("isPrivate", isPrivate);
        attributes.put("contentGroupIds", contentGroupIds);
        return attributes;
    }
}
