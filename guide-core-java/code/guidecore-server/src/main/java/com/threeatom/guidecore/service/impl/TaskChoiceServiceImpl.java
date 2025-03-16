
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.request.ChoiceDto;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.mapper.TaskChoiceMapper;
import com.threeatom.guidecore.service.TaskChoiceService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class TaskChoiceServiceImpl extends ServiceImpl<TaskChoiceMapper, TaskChoice> implements TaskChoiceService {

    @Override
    @Transactional
    public Map<Integer, Integer> createTaskChoices(List<ChoiceDto> choiceDtos, Task task) {
        if (TaskType.OPEN_QUESTION.equals(task.getType())) {
            return Map.of();
        }
        validateAllChoiceIdsAreNegative(choiceDtos);

        Map<Integer, Integer> tempChoiceIdToChoiceId = new HashMap<>();
        for (int i = 0; i < choiceDtos.size(); i++) {
            ChoiceDto choiceDto = choiceDtos.get(i);
            TaskChoice taskChoice = createTaskChoice(task.getId(), choiceDto.getText(), i);
            save(taskChoice);
            tempChoiceIdToChoiceId.put(choiceDto.getId(), taskChoice.getId());
        }

        return tempChoiceIdToChoiceId;
    }

    @Override
    @Transactional
    public Map<Integer, Integer> updateTaskChoices(List<TaskChoice> choices, List<ChoiceDto> choiceDtos, Integer taskId) {
        if (CollectionUtils.isEmpty(choices) && CollectionUtils.isEmpty(choiceDtos)) {
            return Map.of();
        }

        Map<Integer, TaskChoice> idToExistingChoice = idToTaskChoice(choices);
        Set<Integer> choiceDtoIds = choiceDtoIds(choiceDtos);
        List<TaskChoice> choicesToRemove = taskChoicesToRemove(idToExistingChoice, choiceDtoIds);
        updateBatchById(choicesToRemove);

        choiceDtoIds.removeAll(idToExistingChoice.keySet());
        return saveOrUpdateTaskChoices(choiceDtos, taskId, choiceDtoIds, idToExistingChoice);
    }

    private void validateAllChoiceIdsAreNegative(List<ChoiceDto> choiceDtos) {
        boolean anyPositiveId = choiceDtos.stream()
            .map(ChoiceDto::getId)
            .anyMatch(choiceDtoId -> choiceDtoId > 0);

        if (anyPositiveId) {
            throw new ValidationException("ChoiceDto id must be negative for new choices");
        }
    }

    private TaskChoice createTaskChoice(Integer taskId, String content, int order) {
        TaskChoice taskChoice = new TaskChoice();
        taskChoice.setTaskId(taskId);
        taskChoice.setContent(content);
        taskChoice.setOrder(order);
        return taskChoice;
    }

    private Map<Integer, TaskChoice> idToTaskChoice(List<TaskChoice> choices) {
        return choices.stream().collect(Collectors.toMap(TaskChoice::getId, Function.identity()));
    }

    private Set<Integer> choiceDtoIds(List<ChoiceDto> choiceDtos) {
        return choiceDtos.stream()
            .map(ChoiceDto::getId)
            .collect(Collectors.toSet());
    }

    private List<TaskChoice> taskChoicesToRemove(Map<Integer, TaskChoice> idToExistingChoice,
                                                 Set<Integer> choiceDtoIds) {
        Set<Integer> taskChoiceIdToRemove = choiceIdsToRemove(idToExistingChoice, choiceDtoIds);

        return idToExistingChoice.entrySet().stream()
            .filter(entry -> taskChoiceIdToRemove.contains(entry.getKey()))
            .map(entry -> updateTaskChoiceToBeRemoved(entry.getValue()))
            .collect(Collectors.toList());
    }

    private Set<Integer> choiceIdsToRemove(Map<Integer, TaskChoice> idToExistingChoice, Set<Integer> choiceDtoIds) {
        Set<Integer> taskChoiceIdToRemove = new HashSet<>(idToExistingChoice.keySet());
        choiceDtoIds.forEach(taskChoiceIdToRemove::remove);
        return taskChoiceIdToRemove;
    }

    private TaskChoice updateTaskChoiceToBeRemoved(TaskChoice taskChoice) {
        taskChoice.setIsDeleted(true);
        taskChoice.setOrder(-1);
        return taskChoice;
    }

    private Map<Integer, Integer> saveOrUpdateTaskChoices(List<ChoiceDto> choiceDtos, Integer taskId,
                                                          Set<Integer> tempIdsForNewTaskChoices,
                                                          Map<Integer, TaskChoice> idToExistingChoice) {
        Map<Integer, Integer> taskChoiceDtoIdToTaskChoiceId = new HashMap<>();

        for (int i = 0; i < choiceDtos.size(); i++) {
            ChoiceDto choiceDto = choiceDtos.get(i);
            if (tempIdsForNewTaskChoices.contains(choiceDto.getId())) {
                TaskChoice taskChoice = createTaskChoice(taskId, choiceDto.getText(), i);
                save(taskChoice);
                taskChoiceDtoIdToTaskChoiceId.put(choiceDto.getId(), taskChoice.getId());
                continue;
            }

            TaskChoice taskChoice = idToExistingChoice.get(choiceDto.getId());
            taskChoice.setOrder(i);
            taskChoice.setContent(choiceDto.getText());
            updateById(taskChoice);
            taskChoiceDtoIdToTaskChoiceId.put(choiceDto.getId(), taskChoice.getId());
        }

        return taskChoiceDtoIdToTaskChoiceId;
    }
}
