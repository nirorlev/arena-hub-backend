package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.TaskType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDto {
    private TaskType type;
    private String text;
    private List<List<Integer>> grouping;
    private Boolean randomChoiceOrder;
    private Integer minRequiredPairs;

    private List<ChoiceDto> choices;
}
