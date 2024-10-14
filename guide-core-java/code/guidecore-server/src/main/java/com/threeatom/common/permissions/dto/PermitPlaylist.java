package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.enums.AuthorizationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitPlaylist extends PermitCollection {

    @Override
    public AuthorizationResourceType getType() {
        return AuthorizationResourceType.PLAYLIST;
    }
}
