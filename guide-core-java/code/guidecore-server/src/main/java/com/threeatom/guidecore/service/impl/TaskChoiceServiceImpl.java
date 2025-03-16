
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
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
