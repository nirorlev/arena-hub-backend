package com.threeatom.common.permit.dto;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUser {
    private String id;
    private boolean isOrgAdmin;
    private List<String> contentGroupIds;
    private List<String> managedContentGroupIds;

    public HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("id", id);
        attributes.put("isOrgAdmin", isOrgAdmin);
        attributes.put("contentGroupIds", contentGroupIds);
        attributes.put("managedContentGroupIds", managedContentGroupIds);
        return attributes;
    }
}
