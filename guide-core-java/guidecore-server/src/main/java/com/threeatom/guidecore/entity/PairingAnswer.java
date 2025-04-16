package com.threeatom.guidecore.entity;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PairingAnswer extends Answer {
    private List<List<Integer>> choiceIds;

    public PairingAnswer(Answer answer) {
        super();
        PairingAnswer pairingAnswer = cast(answer);
        this.choiceIds = pairingAnswer.getChoiceIds();
    }

    private PairingAnswer cast(Answer answer) {
        return (PairingAnswer) answer;
    }
}