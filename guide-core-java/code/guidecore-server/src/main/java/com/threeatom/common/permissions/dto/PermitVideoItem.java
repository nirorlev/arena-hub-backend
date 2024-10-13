package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.AuthorisationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitVideoItem extends PermitContentItem {

    @Override
    public AuthorisationResourceType getType() {
        return AuthorisationResourceType.VIDEO_ITEM;
    }
}
