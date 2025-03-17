package com.threeatom.guidecore.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleChoiceProperties implements TaskProperties {
    private boolean randomOrder;
}
