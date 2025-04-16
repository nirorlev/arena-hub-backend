package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.ValidateTaskProperties;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.enums.TaskType;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

public class TaskPropertiesValidator implements ConstraintValidator<ValidateTaskProperties, QuestionDto> {

    public static final List<TaskType> RANDOM_ORDER_TASK_TYPES =
        List.of(TaskType.MULTIPLE_CHOICE, TaskType.PAIRING, TaskType.SINGLE_CHOICE);

    @Override
    public boolean isValid(QuestionDto questionDto, ConstraintValidatorContext context) {
        boolean result = true;
        if (RANDOM_ORDER_TASK_TYPES.contains(questionDto.getType())) {
            result &= questionDto.getRandomChoiceOrder() != null;
        }

        if (TaskType.PAIRING.equals(questionDto.getType())) {
            result &= !CollectionUtils.isEmpty(questionDto.getGrouping()) && questionDto.getMinRequiredPairs() != null;
        }

        return result;
    }
}
