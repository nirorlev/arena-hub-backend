package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.enums.AuthorizationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitVideoItem extends PermitContentItem {

    @Override
    public AuthorizationResourceType getType() {
        return AuthorizationResourceType.VIDEO_ITEM;
    }
}
