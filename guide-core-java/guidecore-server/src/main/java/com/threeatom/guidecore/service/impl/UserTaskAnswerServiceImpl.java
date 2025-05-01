package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.response.ProgressDetailsDto;
import com.threeatom.guidecore.dto.response.TaskProgressDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDetailDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.mapper.UserTaskAnswerMapper;
import com.threeatom.guidecore.mapping.UserTaskAnswerMapping;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceFillInBlankService;
import com.threeatom.guidecore.service.UserTaskAnswerChoicePairingService;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceService;
import com.threeatom.guidecore.service.UserTaskAnswerReviewService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import com.threeatom.guidecore.service.validation.TaskAnswerChoiceValidator;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTaskAnswerServiceImpl extends ServiceImpl<UserTaskAnswerMapper, UserTaskAnswer>
    implements UserTaskAnswerService {

    private static final int TASK_COMPLETION_PERCENT = 60;

    private final VideoAnswerStrategy videoAnswerStrategy;
    private final UserTaskAnswerChoicePairingService userTaskAnswerChoicePairingService;
    private final UserTaskAnswerChoiceFillInBlankService userTaskAnswerChoiceFillInBlankService;
    private final UserTaskAnswerChoiceService userTaskAnswerChoiceService;
    private final UserTaskAnswerMapping userTaskAnswerMapping;
    private final UserTaskAnswerReviewService userTaskAnswerReviewService;

    @Override
    public UserTaskAnswersDto findUserTaskAnswersByTaskIdAndUserId(Integer taskId, TaskType taskType,
                                                                   OffsetDateTime startDate, OffsetDateTime endDate,
                                                                   PortalUser portalUser) {
        List<UserTaskAnswer> taskAnswers = baseMapper.findTaskAnswers(taskId, taskType.name(), portalUser.getUserId(),
            startDate, endDate);

        return convertUserTaskAnswersDtos(taskType, taskAnswers);
    }

    @Override
    public UserTaskAnswersDto findUserTaskAnswersByTaskId(Integer taskId, TaskType taskType, OffsetDateTime startDate,
                                                          OffsetDateTime endDate) {
        List<UserTaskAnswer> taskAnswers =
            baseMapper.findTaskAnswers(taskId, taskType.name(), null, startDate, endDate);

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
    public Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgress(List<Integer> taskIds,
                                                                              OffsetDateTime startDate,
                                                                              PortalUser portalUser) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Map.of();
        }

        List<UserTaskAnswer> userTaskAnswers =
            baseMapper.findTaskAnswersByTaskIds(taskIds, startDate, portalUser.getUserId());
        Map<Integer, UserTaskAnswer> taskIdToUserAnswer = userTaskAnswers.stream()
            .collect(Collectors.groupingBy(UserTaskAnswer::getTaskId,
                Collectors.collectingAndThen(Collectors.toList(), answers -> answers.get(0))));

        return taskIds.stream().collect(
            Collectors.toMap(Function.identity(),
                taskId -> taskProgressDto(taskIdToUserAnswer, taskId)));
    }

    @Override
    @Transactional
    public UserTaskAnswer createAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto,
                                       Task task, CourseEnrollment activeEnrollment, PortalUser portalUser) {
        verifyTaskVersion(userTaskAnswerDto, task);
        verifyAnswersCountInEnrollment(task, activeEnrollment, portalUser);

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

    private void verifyAnswersCountInEnrollment(Task task, CourseEnrollment activeEnrollment, PortalUser portalUser) {
        if (task.getRetries() > 0 &&
            userAnswerCountExceedsLimit(task, portalUser.getUserId(), activeEnrollment.getStartDate())) {

            log.info("Task retries exceeded for task %s".formatted(task.getId()));
            throw new ValidationException("Task retries exceeded");
        }
    }

    private void verifyTaskVersion(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto, Task task) {
        if (!task.getVersion().equals(userTaskAnswerDto.getTaskVersion())) {
            log.error("Task version mismatch for task %s".formatted(task.getId()));
            throw new ValidationException("Task version mismatch");
        }
    }

    private boolean userAnswerCountExceedsLimit(Task task, Integer userId, OffsetDateTime startDate) {
        int answersCount = countByTaskIdAndUserId(task.getId(), userId, startDate);
        return answersCount >= task.getRetries();
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
        Set<Integer> taskChoiceIds = getTaskChoiceIds(task);

        switch (task.getType()) {
            case PAIRING ->
                createPairingUserTaskAnswer(task, (PairingAnswer) userAnswer, userTaskAnswerId, taskChoiceIds);
            case MULTIPLE_CHOICE ->
                createMultipleChoiceUserTaskAnswer(task, (MultipleChoiceAnswer) userAnswer, userTaskAnswerId,
                    taskChoiceIds);
            case SINGLE_CHOICE ->
                createSingleChoiceUserTaskAnswer(task, (SingleChoiceAnswer) userAnswer, userTaskAnswerId,
                    taskChoiceIds);
            case FILL_IN_THE_BLANK ->
                createFillInTheBlanksUserTaskAnswer(task, (FillInTheBlankAnswer) userAnswer, userTaskAnswerId,
                    taskChoiceIds);
        }
    }

    private void createFillInTheBlanksUserTaskAnswer(Task task, FillInTheBlankAnswer userAnswer,
                                                     Integer userTaskAnswerId,
                                                     Set<Integer> taskChoiceIds) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsSameAsUserSelectedChoiceIds(
            taskChoiceIds, new HashSet<>(userAnswer.getKeywordToAnswer().values()), task.getId());

        List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks =
            userTaskAnswerChoiceFillInBlankService.createAnswer(userAnswer,
                (FillInTheBlankAnswer) task.getAnswer(), userTaskAnswerId);
        userTaskAnswerReviewService.createFillInTheBlankAnswerReview(userTaskAnswerChoiceFillInBlanks,
            userTaskAnswerId);
    }

    private void createSingleChoiceUserTaskAnswer(Task task, SingleChoiceAnswer userAnswer, Integer userTaskAnswerId,
                                                  Set<Integer> taskChoiceIds) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsContainsUserSelectedChoiceIds(
            taskChoiceIds, Set.of(userAnswer.getChoiceId()), task.getId());

        UserTaskAnswerChoice userTaskAnswerChoice =
            userTaskAnswerChoiceService.createAnswer(userAnswer, task, userTaskAnswerId);
        userTaskAnswerReviewService.createSingleChoiceAnswerReview(userTaskAnswerChoice, userTaskAnswerId);
    }

    private void createMultipleChoiceUserTaskAnswer(Task task, MultipleChoiceAnswer userAnswer,
                                                    Integer userTaskAnswerId,
                                                    Set<Integer> taskChoiceIds) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsContainsUserSelectedChoiceIds(
            taskChoiceIds, new HashSet<>(userAnswer.getChoiceIds()), task.getId());

        List<UserTaskAnswerChoice> userTaskAnswerChoices =
            userTaskAnswerChoiceService.createAnswer(userAnswer, task, userTaskAnswerId);
        userTaskAnswerReviewService.createMultipleChoiceAnswerReview(userTaskAnswerChoices, userTaskAnswerId);
    }

    private void createPairingUserTaskAnswer(Task task, PairingAnswer userAnswer, Integer userTaskAnswerId,
                                             Set<Integer> taskChoiceIds) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsSameAsUserSelectedChoiceIds(
            taskChoiceIds, getPairingChoiceIds(userAnswer), task.getId());

        PairingProperties pairingProperties = (PairingProperties) task.getProperties();
        List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings =
            userTaskAnswerChoicePairingService.createAnswer(userAnswer, (PairingAnswer) task.getAnswer(),
                pairingProperties, userTaskAnswerId);
        userTaskAnswerReviewService.createPairingAnswerReview(userTaskAnswerChoicePairings, pairingProperties,
            userTaskAnswerId);
    }

    private Set<Integer> getTaskChoiceIds(Task task) {
        return task.getChoices().stream()
            .map(TaskChoice::getId)
            .collect(Collectors.toSet());
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

    private int countByTaskIdAndUserId(Integer taskId, Integer userId, OffsetDateTime fromDate) {
        QueryWrapper<UserTaskAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq("owner_id", userId);
        queryWrapper.ge("created_time", fromDate);
        return count(queryWrapper);
    }

    private Set<Integer> getPairingChoiceIds(PairingAnswer pairingAnswer) {
        return pairingAnswer.getChoiceIds().stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
    }

    private ProgressDetailsDto<TaskProgressDto> taskProgressDto(Map<Integer, UserTaskAnswer> taskIdToUserAnswer,
                                                                Integer taskId) {
        Optional<UserTaskAnswer> userTaskAnswer = Optional.ofNullable(taskIdToUserAnswer.get(taskId));
        ProgressDetailsDto<TaskProgressDto> taskProgressDtoProgressDetailsDto = new ProgressDetailsDto<>();
        TaskProgressDto taskProgressDto = new TaskProgressDto();
        taskProgressDto.setAnswered(userTaskAnswer.isPresent());
        taskProgressDto.setCompleted(
            userTaskAnswer.isPresent() && isTaskCompleted(userTaskAnswer.get()));
        taskProgressDtoProgressDetailsDto.setProgress(taskProgressDto);

        return taskProgressDtoProgressDetailsDto;
    }

    private boolean isTaskCompleted(UserTaskAnswer userTaskAnswer) {
        List<UserTaskAnswerReview> userTaskAnswerReviews = userTaskAnswer.getUserTaskAnswerReviews();
        if (CollectionUtils.isEmpty(userTaskAnswerReviews)) {
            return false;
        }

        return userTaskAnswerReviews.stream()
            .max(Comparator.comparing(UserTaskAnswerReview::getCreatedTime))
            .filter(review -> review.getScore() >= TASK_COMPLETION_PERCENT)
            .isPresent();
    }
}
