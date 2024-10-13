package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.AuthorisationResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitPlaylist extends PermitCollection {

    @Override
    public AuthorisationResourceType getType() {
        return AuthorisationResourceType.PLAYLIST;
    }
}
