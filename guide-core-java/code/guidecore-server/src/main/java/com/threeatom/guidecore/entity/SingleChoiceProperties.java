package com.threeatom.guidecore.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleChoiceProperties implements TaskProperties {
    private boolean randomOrder;
}
