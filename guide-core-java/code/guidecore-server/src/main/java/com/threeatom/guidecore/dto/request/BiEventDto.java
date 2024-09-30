package com.threeatom.guidecore.dto.request;

import com.threeatom.guidecore.enums.BiEventAction;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class BiEventDto {
    private Integer userId;
    private BiEventAction action;
}
