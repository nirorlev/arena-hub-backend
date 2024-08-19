package com.threeatom.common.permit.dto;

import com.threeatom.guidecore.constant.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitPlaylist extends PermitCollection {

    @Override
    public String getType() {
        return ResourceType.playList;
    }
}
