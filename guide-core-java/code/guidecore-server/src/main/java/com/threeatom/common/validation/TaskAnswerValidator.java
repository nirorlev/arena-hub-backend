package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.ValidateTaskAnswer;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.enums.TaskType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaskAnswerValidator implements ConstraintValidator<ValidateTaskAnswer, QuestionDto> {

    @Override
    public boolean isValid(QuestionDto questionDto, ConstraintValidatorContext context) {
        return !TaskType.OPEN_QUESTION.equals(questionDto.getType()) && questionDto.getAnswer() != null;
    }
}
