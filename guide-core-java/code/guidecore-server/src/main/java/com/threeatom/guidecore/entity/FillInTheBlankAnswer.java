package com.threeatom.guidecore.entity;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FillInTheBlankAnswer extends Answer {
    private Map<String, Integer> keywordToAnswer;

    public FillInTheBlankAnswer(Answer answer) {
        super();
        FillInTheBlankAnswer fillInTheBlankAnswer = cast(answer);
        this.keywordToAnswer = new HashMap<>(fillInTheBlankAnswer.getKeywordToAnswer());
    }

    private FillInTheBlankAnswer cast(Answer answer) {
        return (FillInTheBlankAnswer) answer;
    }
}
