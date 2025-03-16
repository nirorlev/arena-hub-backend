package com.threeatom.guidecore.entity;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FillInTheBlankAnswer extends Answer {
    private Map<String, Integer> keywordToAnswer;
}
