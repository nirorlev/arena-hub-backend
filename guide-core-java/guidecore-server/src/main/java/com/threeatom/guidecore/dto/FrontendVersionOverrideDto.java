package com.threeatom.guidecore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrontendVersionOverrideDto {
    private String version;
    private boolean isFEOverrideUsed;
}
