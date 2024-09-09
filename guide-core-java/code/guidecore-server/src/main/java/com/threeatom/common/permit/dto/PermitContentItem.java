package com.threeatom.common.permit.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitContentItem extends PermitResource {
    private String ownerId;
    private boolean isPublic;
    private boolean isPrivate;
    private Set<String> contentGroupIds;
}
