package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.mapper.TaskMapper;
import com.threeatom.guidecore.mapping.TaskMapping;
import com.threeatom.guidecore.service.TaskChoiceService;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskMapping taskMapping;
    private final TaskChoiceService taskChoiceService;
    private final VideoAnswerStrategy videoAnswerStrategy;
    private final TaskPropertiesStrategy taskPropertiesStrategy;

    @Override
    public List<TaskDto> videoTasks(List<VideoEvent> taskVideoEvents) {
        return taskMapping.map(taskVideoEvents);
    }

    @Override
    @Transactional
    public void createTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, VideoEvent videoEvent,
                           PortalUser portalUser) {
        Task task = new Task();
        com.threeatom.guidecore.dto.request.QuestionDto question = taskDto.getQuestion();
        task.setVideoEventId(videoEvent.getId());
        task.setQuestion(question.getText());

        task.setAllowSkip(taskDto.getCanSkip());
        task.setOwnerId(portalUser.getUserId());
        task.setUpdatedByUserId(portalUser.getUserId());
        task.setType(question.getType());
        task.setRetries(taskDto.getRetries());

        save(task);

        Map<Integer, Integer> tempChoiceIdToRealChoiceId =
            taskChoiceService.createTaskChoices(question.getChoices(), task);
        task.setProperties(
            taskPropertiesStrategy.createProperties(task.getType(), question, tempChoiceIdToRealChoiceId));
        task.setAnswer(videoAnswerStrategy.createAnswer(question, tempChoiceIdToRealChoiceId));

        updateById(task);
    }
}
