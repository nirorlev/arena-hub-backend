package com.threeatom.guidecore.service.validation;

import com.threeatom.common.exception.ValidationException;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class TaskAnswerChoiceValidator {

    public void validateTaskChoiceIdsContainsUserSelectedChoiceIds(Set<Integer> taskChoiceIds,
                                                                   Set<Integer> userChoiceIds,
                                                                   Integer taskId) {

        if (!taskChoiceIds.containsAll(userChoiceIds)) {
            log.error("Invalid choice ids for task {}", taskId);
            throw new ValidationException("Invalid choice ids passed for the task");
        }
    }

    public void validateTaskChoiceIdsSameAsUserSelectedChoiceIds(Set<Integer> taskChoiceIds, Set<Integer> userChoiceIds,
                                                                 Integer taskId) {

        if (taskChoiceIds.size() != userChoiceIds.size() || !taskChoiceIds.containsAll(userChoiceIds)) {
            log.error("Choice ids are not same as task choice ids for task {}", taskId);
            throw new ValidationException("Choice ids should have all task choice ids");
        }
    }
}
