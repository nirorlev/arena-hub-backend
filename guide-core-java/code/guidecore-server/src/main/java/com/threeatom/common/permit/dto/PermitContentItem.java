package com.threeatom.common.permit.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PermitContentItem extends PermitItem {
    private String ownerId;
    private boolean isPublic;
    private boolean isPrivate;
    private List<String> contentGroupIds;
}
