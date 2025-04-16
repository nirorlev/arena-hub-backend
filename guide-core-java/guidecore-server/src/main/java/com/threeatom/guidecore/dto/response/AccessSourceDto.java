package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.SourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessSourceDto {
    private String id;
    private String type;
    private Boolean canPublish;
    private String slug;
}
