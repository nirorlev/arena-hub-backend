
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.ChoiceDto;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import java.util.List;
import java.util.Map;

public interface TaskChoiceService extends IService<TaskChoice> {
    Map<Integer, Integer> createTaskChoices(List<ChoiceDto> choices, Task task);

    Map<Integer, Integer> updateTaskChoices(List<TaskChoice> choices, List<ChoiceDto> choiceDtos, Integer taskId);
}
