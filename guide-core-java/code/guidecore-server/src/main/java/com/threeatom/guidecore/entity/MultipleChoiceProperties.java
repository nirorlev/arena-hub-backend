package com.threeatom.guidecore.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoiceProperties implements TaskProperties {
    private boolean randomOrder;
}
