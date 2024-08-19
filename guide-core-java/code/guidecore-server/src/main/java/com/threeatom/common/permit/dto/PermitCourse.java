package com.threeatom.common.permit.dto;

import com.threeatom.guidecore.constant.ResourceType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCourse extends PermitCollection {

    @Override
    public String getType() {
        return ResourceType.course;
    }
}
