package com.threeatom.guidecore.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OpenQuestionAnswer extends Answer {
    private String content;
}
