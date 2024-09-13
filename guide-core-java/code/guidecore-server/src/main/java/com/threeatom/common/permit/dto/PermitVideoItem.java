package com.threeatom.common.permit.dto;

import com.threeatom.guidecore.constant.ResourceType;
import java.util.HashMap;
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
