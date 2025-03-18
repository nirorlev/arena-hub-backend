package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.ValidateTaskChoices;
import com.threeatom.guidecore.dto.request.ChoiceDto;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.enums.TaskType;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaskChoicesValidator implements ConstraintValidator<ValidateTaskChoices, QuestionDto> {
    @Override
    public boolean isValid(QuestionDto questionDto, ConstraintValidatorContext context) {
        List<ChoiceDto> choices = questionDto.getChoices();

        if (TaskType.OPEN_QUESTION.equals(questionDto.getType())) {
            return choices.isEmpty();
        }

        return !choices.isEmpty() && uniqueChoiceIdsUsed(choices);
    }

    private boolean uniqueChoiceIdsUsed(List<ChoiceDto> choices) {
        return choices.size() == choices.stream().map(ChoiceDto::getId).collect(Collectors.toSet()).size();
    }
}
