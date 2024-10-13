package com.threeatom.common.permissions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUser {
    private String id;
    private boolean isOrgAdmin;
    private Set<String> contentGroupIds;
    private Set<String> managedContentGroupIds;

    @JsonIgnore
    public HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("key", id);
        attributes.put("isOrgAdmin", isOrgAdmin);
        attributes.put("contentGroupIds", contentGroupIds);
        attributes.put("managedContentGroupIds", managedContentGroupIds);
        return attributes;
    }
}
