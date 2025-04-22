package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDto {
    private String name;
    private String parentGroupCode;
    private Map<String, Boolean> permissions;
}
