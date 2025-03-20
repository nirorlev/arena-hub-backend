
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDetailDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.GcUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.mapper.UserTaskAnswerMapper;
import com.threeatom.guidecore.mapping.UserTaskAnswerMapping;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceFillInBlankService;
import com.threeatom.guidecore.service.UserTaskAnswerChoicePairingService;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTaskAnswerServiceImpl extends ServiceImpl<UserTaskAnswerMapper, UserTaskAnswer>
    implements UserTaskAnswerService {

    private final VideoAnswerStrategy videoAnswerStrategy;
    private final UserTaskAnswerChoicePairingService userTaskAnswerChoicePairingService;
    private final UserTaskAnswerChoiceFillInBlankService userTaskAnswerChoiceFillInBlankService;
    private final UserTaskAnswerChoiceService userTaskAnswerChoiceService;
    private final UserTaskAnswerMapping userTaskAnswerMapping;

    @Override
    public UserTaskAnswersDto findUserTaskAnswersByTaskIdAndUserId(Integer taskId, TaskType taskType,
                                                                   PortalUser portalUser) {
        List<UserTaskAnswer> taskAnswers = baseMapper.findTaskAnswers(taskId, taskType.name(), portalUser.getUserId());

        return convertUserTaskAnswersDtos(taskType, taskAnswers);
    }

    @Override
    public UserTaskAnswersDto findUserTaskAnswersByTaskId(Integer taskId, TaskType taskType) {
        List<UserTaskAnswer> taskAnswers = baseMapper.findTaskAnswers(taskId, taskType.name(), null);

        return convertUserTaskAnswersDtos(taskType, taskAnswers);
    }

    @Override
    public UserTaskAnswerDto findUserTaskAnswerById(Integer userTaskAnswerId, TaskType taskType) {
        UserTaskAnswer userTaskAnswer = Optional.ofNullable(baseMapper.findById(userTaskAnswerId, taskType.name()))
            .orElseThrow(() -> {
                log.error("User task answer not found for id %s".formatted(userTaskAnswerId));
                return new ResourceNotFoundException("User task answer not found");
            });

        return userTaskAnswerMapping.mapUserAnswer(userTaskAnswer);
    }

    @Override
    @Transactional
    public UserTaskAnswer createAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto,
                                       Task task, PortalUser portalUser) {
        if (!task.getVersion().equals(userTaskAnswerDto.getTaskVersion())) {
            log.error("Task version mismatch for task %s".formatted(task.getId()));
            throw new ValidationException("Task version mismatch");
        }
        List<UserTaskAnswer> answers = findByTaskIdAndUserId(task.getId(), portalUser.getUserId());
        if (answers.size() >= task.getRetries()) {
            log.info("Task retries exceeded for task %s".formatted(task.getId()));
            throw new ValidationException("Task retries exceeded");
        }

        try {
            Answer answer = videoAnswerStrategy.createAnswer(task.getType(), userTaskAnswerDto.getAnswer());
            UserTaskAnswer userTaskAnswer = userTaskAnswer(portalUser, task, answer);
            save(userTaskAnswer);

            createUserTaskAnswerByType(task, answer, userTaskAnswer.getId());

            return userTaskAnswer;
        } catch (JsonProcessingException e) {
            log.error("Error serializing Answer object to JSON for task %s".formatted(task.getId()), e);
            throw new ValidationException("Invalid answer format for type task " + task.getType());
        }
    }

    private UserTaskAnswersDto convertUserTaskAnswersDtos(TaskType taskType, List<UserTaskAnswer> userTaskAnswers) {
        UserTaskAnswersDto userTaskAnswersDto = new UserTaskAnswersDto();
        userTaskAnswersDto.setTaskType(taskType);
        userTaskAnswersDto.setUsers(userIdToUserTaskAnswerDetailDto(userIdToUserTaskAnswers(userTaskAnswers)));

        return userTaskAnswersDto;
    }

    private Map<Integer, List<UserTaskAnswer>> userIdToUserTaskAnswers(List<UserTaskAnswer> taskAnswers) {
        return taskAnswers.stream()
            .collect(Collectors.groupingBy(UserTaskAnswer::getOwnerId));
    }

    private Map<String, UserTaskAnswerDetailDto> userIdToUserTaskAnswerDetailDto(
        Map<Integer, List<UserTaskAnswer>> userIdToUserTaskAnswers) {
        return userIdToUserTaskAnswers.entrySet().stream()
            .collect(Collectors.toMap(userIdToUserTaskAnswer -> String.valueOf(userIdToUserTaskAnswer.getKey()),
                userIdToUserTaskAnswer -> convertToTaskAnswerDetailDto(userIdToUserTaskAnswer.getValue())));
    }

    private UserTaskAnswerDetailDto convertToTaskAnswerDetailDto(List<UserTaskAnswer> userTaskAnswers) {
        GcUser taskAnswersOwner = userTaskAnswers.get(0).getOwner();

        UserTaskAnswerDetailDto userTaskAnswerDetailDto = userTaskAnswerMapping.mapUserAnswerDetails(taskAnswersOwner);
        userTaskAnswerDetailDto.setAnswers(userTaskAnswerMapping.mapUserAnswers(userTaskAnswers));
        return userTaskAnswerDetailDto;
    }

    private void createUserTaskAnswerByType(Task task, Answer userAnswer, Integer userTaskAnswerId) {
        Set<Integer> taskChoiceIds = task.getChoices().stream()
            .map(TaskChoice::getId)
            .collect(Collectors.toSet());

        switch (task.getType()) {
            case PAIRING -> {
                PairingAnswer userPairingAnswer = (PairingAnswer) userAnswer;
                validateTaskChoiceIdsSameAsUserSelectedChoiceIds(taskChoiceIds, getPairingChoiceIds(userPairingAnswer),
                    task.getId());
                userTaskAnswerChoicePairingService.createAnswer(userPairingAnswer, (PairingAnswer) task.getAnswer(),
                    (PairingProperties) task.getProperties(), userTaskAnswerId);
            }
            case MULTIPLE_CHOICE -> {
                MultipleChoiceAnswer userMultipleChoiceAnswer = (MultipleChoiceAnswer) userAnswer;
                validateTaskChoiceIdsContainsUserSelectedChoiceIds(taskChoiceIds,
                    new HashSet<>(userMultipleChoiceAnswer.getChoiceIds()), task.getId());
                userTaskAnswerChoiceService.createAnswer(userMultipleChoiceAnswer, task, userTaskAnswerId);
            }
            case SINGLE_CHOICE -> {
                SingleChoiceAnswer userSingleChoiceAnswer = (SingleChoiceAnswer) userAnswer;
                validateTaskChoiceIdsContainsUserSelectedChoiceIds(taskChoiceIds,
                    Set.of(userSingleChoiceAnswer.getChoiceId()), task.getId());
                userTaskAnswerChoiceService.createAnswer(userSingleChoiceAnswer, task, userTaskAnswerId);
            }
            case FILL_IN_THE_BLANK -> {
                FillInTheBlankAnswer fillInTheBlankAnswer = (FillInTheBlankAnswer) userAnswer;
                validateTaskChoiceIdsSameAsUserSelectedChoiceIds(taskChoiceIds,
                    new HashSet<>(fillInTheBlankAnswer.getKeywordToAnswer().values()), task.getId());
                userTaskAnswerChoiceFillInBlankService.createAnswer(fillInTheBlankAnswer,
                    (FillInTheBlankAnswer) task.getAnswer(), userTaskAnswerId);
            }
        }
    }

    private UserTaskAnswer userTaskAnswer(PortalUser portalUser, Task task, Answer answer)
        throws JsonProcessingException {
        UserTaskAnswer userTaskAnswer = new UserTaskAnswer();
        userTaskAnswer.setTaskId(task.getId());
        userTaskAnswer.setOwnerId(portalUser.getUserId());
        userTaskAnswer.setContent(answer);
        userTaskAnswer.setTaskVersion(task.getVersion());
        return userTaskAnswer;
    }

    private List<UserTaskAnswer> findByTaskIdAndUserId(Integer taskId, Integer userId) {
        QueryWrapper<UserTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq("owner_id", userId);
        return list(queryWrapper);
    }

    private Set<Integer> getPairingChoiceIds(PairingAnswer pairingAnswer) {
        return pairingAnswer.getChoiceIds().stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
    }

    private void validateTaskChoiceIdsContainsUserSelectedChoiceIds(Set<Integer> taskChoiceIds,
                                                                    Set<Integer> userChoiceIds,
                                                                    Integer taskId) {

        if (!taskChoiceIds.containsAll(userChoiceIds)) {
            log.error("Invalid choice ids for task {}", taskId);
            throw new ValidationException("Invalid choice ids passed for the task");
        }
    }

    private void validateTaskChoiceIdsSameAsUserSelectedChoiceIds(Set<Integer> taskChoiceIds,
                                                                  Set<Integer> userChoiceIds,
                                                                  Integer taskId) {

        if (taskChoiceIds.size() != userChoiceIds.size() || !taskChoiceIds.containsAll(userChoiceIds)) {
            log.error("Choice ids are not same as task choice ids for task {}", taskId);
            throw new ValidationException("Choice ids should have all task choice ids");
        }
    }
}
