package com.threeatom.common.permissions.dto;

import com.threeatom.guidecore.constant.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitVideoItem extends PermitContentItem {

    @Override
    public String getType() {
        return ResourceType.videoItem;
    }
}
