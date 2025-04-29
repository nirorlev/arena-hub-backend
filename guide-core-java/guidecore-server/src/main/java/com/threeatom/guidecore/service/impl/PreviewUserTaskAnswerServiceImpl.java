package com.threeatom.guidecore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
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
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.mapping.UserTaskAnswerMapping;
import com.threeatom.guidecore.service.PreviewUserTaskAnswerService;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceFillInBlankService;
import com.threeatom.guidecore.service.UserTaskAnswerChoicePairingService;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceService;
import com.threeatom.guidecore.service.UserTaskAnswerReviewService;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import com.threeatom.guidecore.service.validation.TaskAnswerChoiceValidator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreviewUserTaskAnswerServiceImpl implements PreviewUserTaskAnswerService {

    private final VideoAnswerStrategy videoAnswerStrategy;
    private final UserTaskAnswerChoicePairingService userTaskAnswerChoicePairingService;
    private final UserTaskAnswerChoiceFillInBlankService userTaskAnswerChoiceFillInBlankService;
    private final UserTaskAnswerChoiceService userTaskAnswerChoiceService;
    private final UserTaskAnswerMapping userTaskAnswerMapping;
    private final UserTaskAnswerReviewService userTaskAnswerReviewService;

    @Override
    public UserTaskAnswerDto getAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto,
                                       Task task, PortalUser portalUser) {
        verifyTaskVersion(userTaskAnswerDto, task);

        try {
            Answer answer = videoAnswerStrategy.createAnswer(task.getType(), userTaskAnswerDto.getAnswer());
            UserTaskAnswer userTaskAnswer = userTaskAnswer(portalUser, task, answer);
            populateUserTaskAnswerByTaskType(task, answer, userTaskAnswer);

            return userTaskAnswerMapping.mapUserAnswer(userTaskAnswer);
        } catch (JsonProcessingException e) {
            log.error("Error serializing Answer object to JSON for task %s".formatted(task.getId()), e);
            throw new ValidationException("Invalid answer format for type task " + task.getType());
        }
    }

    @Override
    public UserTaskAnswersDto getTaskAnswers(TaskType taskType) {
        UserTaskAnswersDto userTaskAnswersDto = new UserTaskAnswersDto();
        userTaskAnswersDto.setTaskType(taskType);
        return userTaskAnswersDto;
    }

    private void populateUserTaskAnswerByTaskType(Task task, Answer userAnswer, UserTaskAnswer userTaskAnswer) {
        Set<Integer> taskChoiceIds = getTaskChoiceIds(task);
        Integer userTaskAnswerId = userTaskAnswer.getId();

        switch (task.getType()) {
            case PAIRING -> populateUserTaskAnswerForPairing(
                task, (PairingAnswer) userAnswer, userTaskAnswer, taskChoiceIds, userTaskAnswerId);
            case MULTIPLE_CHOICE ->
                populateUserTaskAnswerForMultipleChoise(task, (MultipleChoiceAnswer) userAnswer, userTaskAnswer,
                    taskChoiceIds, userTaskAnswerId);
            case SINGLE_CHOICE ->
                populateUserTaskAnswerForSingleChoice(task, (SingleChoiceAnswer) userAnswer, userTaskAnswer,
                    taskChoiceIds, userTaskAnswerId);
            case FILL_IN_THE_BLANK ->
                populateUserTaskAnswerForFillInBlanks(task, (FillInTheBlankAnswer) userAnswer, userTaskAnswer,
                    taskChoiceIds, userTaskAnswerId);
        }
    }

    private void populateUserTaskAnswerForFillInBlanks(Task task, FillInTheBlankAnswer userAnswer,
                                                       UserTaskAnswer userTaskAnswer,
                                                       Set<Integer> taskChoiceIds, Integer userTaskAnswerId) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsSameAsUserSelectedChoiceIds(taskChoiceIds,
            new HashSet<>(userAnswer.getKeywordToAnswer().values()), task.getId());
        List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks =
            userTaskAnswerChoiceFillInBlankService.getAnswer(userAnswer,
                (FillInTheBlankAnswer) task.getAnswer(), userTaskAnswerId);
        UserTaskAnswerReview fillInTheBlankAnswerReview =
            userTaskAnswerReviewService.getFillInTheBlankAnswerReview(userTaskAnswerChoiceFillInBlanks,
                userTaskAnswerId);

        userTaskAnswer.setUserTaskAnswerChoiceFillInBlanks(userTaskAnswerChoiceFillInBlanks);
        userTaskAnswer.setUserTaskAnswerReviews(List.of(fillInTheBlankAnswerReview));
    }

    private void populateUserTaskAnswerForSingleChoice(Task task, SingleChoiceAnswer userAnswer,
                                                       UserTaskAnswer userTaskAnswer,
                                                       Set<Integer> taskChoiceIds, Integer userTaskAnswerId) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsContainsUserSelectedChoiceIds(taskChoiceIds,
            Set.of(userAnswer.getChoiceId()), task.getId());

        UserTaskAnswerChoice userTaskAnswerChoice =
            userTaskAnswerChoiceService.getAnswer(userAnswer, task, userTaskAnswerId);
        UserTaskAnswerReview singleChoiceAnswerReview =
            userTaskAnswerReviewService.getSingleChoiceAnswerReview(userTaskAnswerChoice, userTaskAnswerId);

        userTaskAnswer.setUserTaskAnswerSingleChoice(userTaskAnswerChoice);
        userTaskAnswer.setUserTaskAnswerReviews(List.of(singleChoiceAnswerReview));
    }

    private void populateUserTaskAnswerForMultipleChoise(Task task, MultipleChoiceAnswer userAnswer,
                                                         UserTaskAnswer userTaskAnswer,
                                                         Set<Integer> taskChoiceIds, Integer userTaskAnswerId) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsContainsUserSelectedChoiceIds(taskChoiceIds,
            new HashSet<>(userAnswer.getChoiceIds()), task.getId());
        List<UserTaskAnswerChoice> userTaskAnswerChoices =
            userTaskAnswerChoiceService.getAnswer(userAnswer, task, userTaskAnswerId);
        UserTaskAnswerReview multipleChoiceAnswerReview =
            userTaskAnswerReviewService.getMultipleChoiceAnswerReview(userTaskAnswerChoices, userTaskAnswerId);

        userTaskAnswer.setUserTaskAnswerMultipleChoices(userTaskAnswerChoices);
        userTaskAnswer.setUserTaskAnswerReviews(List.of(multipleChoiceAnswerReview));
    }

    private void populateUserTaskAnswerForPairing(Task task, PairingAnswer userAnswer, UserTaskAnswer userTaskAnswer,
                                                  Set<Integer> taskChoiceIds, Integer userTaskAnswerId) {
        TaskAnswerChoiceValidator.validateTaskChoiceIdsSameAsUserSelectedChoiceIds(
            taskChoiceIds, getPairingChoiceIds(userAnswer), task.getId());

        PairingProperties pairingProperties = (PairingProperties) task.getProperties();
        List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings =
            userTaskAnswerChoicePairingService.getAnswer(userAnswer, (PairingAnswer) task.getAnswer(),
                pairingProperties, userTaskAnswerId);
        UserTaskAnswerReview pairingAnswerReview =
            userTaskAnswerReviewService.getPairingAnswerReview(userTaskAnswerChoicePairings, pairingProperties,
                userTaskAnswerId);

        userTaskAnswer.setUserTaskAnswerChoicePairings(userTaskAnswerChoicePairings);
        userTaskAnswer.setUserTaskAnswerReviews(List.of(pairingAnswerReview));
    }

    private Set<Integer> getTaskChoiceIds(Task task) {
        return task.getChoices().stream()
            .map(TaskChoice::getId)
            .collect(Collectors.toSet());
    }
    private void verifyTaskVersion(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto, Task task) {
        if (!task.getVersion().equals(userTaskAnswerDto.getTaskVersion())) {
            log.error("Task version mismatch for task %s".formatted(task.getId()));
            throw new ValidationException("Task version mismatch");
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

    private Set<Integer> getPairingChoiceIds(PairingAnswer pairingAnswer) {
        return pairingAnswer.getChoiceIds().stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
    }
}
