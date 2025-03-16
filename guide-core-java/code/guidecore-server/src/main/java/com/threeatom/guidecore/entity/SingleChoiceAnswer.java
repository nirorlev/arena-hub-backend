package com.threeatom.guidecore.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SingleChoiceAnswer extends Answer {
    private Integer choiceId;

    public SingleChoiceAnswer(Answer answer) {
        super();
        SingleChoiceAnswer singleChoiceAnswer = cast(answer);
        this.choiceId = singleChoiceAnswer.getChoiceId();
    }

    private SingleChoiceAnswer cast(Answer answer) {
        return (SingleChoiceAnswer) answer;
    }
}
