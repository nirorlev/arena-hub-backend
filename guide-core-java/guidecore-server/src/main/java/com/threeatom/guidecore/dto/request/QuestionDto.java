package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.ValidateTaskAnswer;
import com.threeatom.common.validation.annotation.ValidateTaskChoices;
import com.threeatom.common.validation.annotation.ValidateTaskProperties;
import com.threeatom.guidecore.enums.TaskType;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ValidateTaskChoices
@ValidateTaskProperties
@ValidateTaskAnswer
public class QuestionDto {
    @NotNull(message = "Task type is required")
    private TaskType type;
    @NotNull(message = "Question text is required")
    private String text;

    private String answer;

    private Boolean randomChoiceOrder;
    private Integer minRequiredPairs;
    private List<List<Integer>> grouping;
    private List<@Valid ChoiceDto> choices = new ArrayList<>();
}
