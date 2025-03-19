package com.threeatom.guidecore.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceAnswer extends Answer {
    private List<Integer> choiceIds;

    public MultipleChoiceAnswer(Answer answer) {
        super();
        MultipleChoiceAnswer multipleChoiceAnswer = cast(answer);
        this.choiceIds = new ArrayList<>(multipleChoiceAnswer.getChoiceIds());
    }

    private MultipleChoiceAnswer cast(Answer answer) {
        return (MultipleChoiceAnswer) answer;
    }
}
