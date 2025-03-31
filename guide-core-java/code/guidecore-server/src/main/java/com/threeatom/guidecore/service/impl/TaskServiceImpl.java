package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.request.ChoiceDto;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskAudit;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.mapper.TaskMapper;
import com.threeatom.guidecore.mapping.TaskMapping;
import com.threeatom.guidecore.service.TaskAuditService;
import com.threeatom.guidecore.service.TaskChoiceService;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskMapping taskMapping;
    private final TaskChoiceService taskChoiceService;
    private final VideoAnswerStrategy videoAnswerStrategy;
    private final TaskPropertiesStrategy taskPropertiesStrategy;
    private final TaskAuditService taskAuditService;

    @Override
    public List<TaskDto> videoTasks(List<VideoEvent> taskVideoEvents) {
        return taskMapping.map(taskVideoEvents);
    }

    @Override
    @Transactional
    public void createTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, VideoEvent videoEvent,
                           Integer courseId, PortalUser portalUser) {
        Task task = new Task();
        com.threeatom.guidecore.dto.request.QuestionDto question = taskDto.getQuestion();
        task.setVideoEventId(videoEvent.getId());
        task.setCourseId(courseId);
        task.setQuestion(question.getText());

        task.setAllowSkip(taskDto.getCanSkip());
        task.setOwnerId(portalUser.getUserId());
        task.setUpdatedByUserId(portalUser.getUserId());
        task.setType(question.getType());
        task.setRetries(taskDto.getRetries());
        task.setVersion(0);

        save(task);

        Map<Integer, Integer> tempChoiceIdToRealChoiceId =
            taskChoiceService.createTaskChoices(question.getChoices(), task);
        task.setProperties(
            taskPropertiesStrategy.createProperties(task.getType(), question, tempChoiceIdToRealChoiceId));
        task.setAnswer(videoAnswerStrategy.createAnswer(question, tempChoiceIdToRealChoiceId));

        updateById(task);
    }

    @Override
    public Task getTask(Integer taskId) {
        Optional<Task> optionalTask = findById(taskId);
        if (optionalTask.isEmpty()) {
            log.error("Cannot find task with id {}!", taskId);
            throw new ResourceNotFoundException("Task not found");
        }

        return optionalTask.get();
    }

    @Override
    @Transactional
    public void updateTask(Task task, com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer userId) {
        QuestionDto question = taskDto.getQuestion();
        validateTaskChoiceIdsUsedForUpdate(task.getChoices(), question.getChoices());
        validateTaskTypeMatches(task, question);

        Task initialStateTask = new Task(task
            , videoAnswerStrategy.copyAnswer(task.getType(), task.getAnswer())
            , taskPropertiesStrategy.copyProperties(task.getType(), task.getProperties())
        );

        taskMapping.update(task, taskDto, userId);

        Map<Integer, Integer> tempChoiceIdToChoiceId =
            taskChoiceService.updateTaskChoices(task.getChoices(), question.getChoices(), task.getId());
        task.setProperties(taskPropertiesStrategy.createProperties(task.getType(), question, tempChoiceIdToChoiceId));
        task.setAnswer(videoAnswerStrategy.createAnswer(question, tempChoiceIdToChoiceId));

        if (!initialStateTask.equals(task)) {
            OffsetDateTime dateTime = OffsetDateTime.now();
            task.setUpdatedTime(dateTime);
            task.setVersion(task.getVersion() + 1);

            updateById(task);
            taskAuditService.create(initialStateTask, userId, dateTime);
            return;
        }

        log.warn("Task {} has not been updated since the initial task state is the same!", task.getId());
    }

    @Override
    @Transactional
    public void deleteTask(Task task, Integer userId) {
        taskChoiceService.deleteChoices(task.getChoices());
        OffsetDateTime dateTime = OffsetDateTime.now();

        task.setIsDeleted(true);
        task.setUpdatedByUserId(userId);
        task.setUpdatedTime(dateTime);
        updateById(task);

        taskAuditService.create(task, userId, dateTime);
    }

    @Override
    public AnswerKeyDto answerKey(Task task) {
        return taskMapping.toAnswerKeyDto(task);
    }

    @Override
    public List<TaskVersionDto> taskVersions(Task currentVersion) {
        List<TaskAudit> taskAuditList = taskAuditService.findByTaskId(currentVersion.getId());
        List<TaskVersionDto> taskVersions = taskMapping.mapTaskVersions(taskAuditList);
        taskVersions.add(0, taskMapping.mapTaskVersion(currentVersion));

        return taskVersions;
    }

    private Optional<Task> findById(Integer taskId) {
        return Optional.ofNullable(baseMapper.findById(taskId));
    }

    private void validateTaskTypeMatches(Task task, QuestionDto question) {
        if (!task.getType().equals(question.getType())) {
            log.warn("Failed to updated the task due to type mismatch. Task id {}, requested type {}, current type {}",
                task.getId(), question.getType(), task.getType());
            throw new ValidationException("Task type cannot be changed!");
        }
    }

    private void validateTaskChoiceIdsUsedForUpdate(List<TaskChoice> choices, List<ChoiceDto> choiceDtos) {
        Set<Integer> taskChoiceIds = choices.stream()
            .map(TaskChoice::getId)
            .collect(Collectors.toSet());

        boolean allPositiveChoiceIdsPresentInTask = choiceDtos.stream()
            .map(ChoiceDto::getId)
            .filter(choiceDtoId -> choiceDtoId > 0)
            .allMatch(taskChoiceIds::contains);

        if (!allPositiveChoiceIdsPresentInTask) {
            throw new ValidationException("To update task choice - the existing ID values should be used");
        }
    }

}
