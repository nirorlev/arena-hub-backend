package com.threeatom.guidecore.dto.request;

import com.threeatom.guidecore.enums.BiEvent;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class BiEventDto {
    private Integer powtoonUserId;
    private String visitorId;
    private Map<String, String> additionalData;
    private BiEvent action;
}
