package com.threeatom.guidecore.entity;


import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultipleChoiceAnswer extends Answer {
    private List<Integer> choiceIds;
}
