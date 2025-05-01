package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.mapper.UserTaskAnswerChoiceMapper;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTaskAnswerChoiceServiceImpl extends ServiceImpl<UserTaskAnswerChoiceMapper, UserTaskAnswerChoice>
    implements UserTaskAnswerChoiceService {

    @Override
    @Transactional
    public List<UserTaskAnswerChoice> createAnswer(MultipleChoiceAnswer userAnswer, Task task,
                                                   Integer userTaskAnswerId) {
        List<UserTaskAnswerChoice> userTaskAnswerChoices = getAnswer(userAnswer, task, userTaskAnswerId);
        saveBatch(userTaskAnswerChoices);
        return userTaskAnswerChoices;
    }

    @Override
    @Transactional
    public UserTaskAnswerChoice createAnswer(SingleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId) {
        UserTaskAnswerChoice answer = getAnswer(userAnswer, task, userTaskAnswerId);
        save(answer);

        return answer;
    }

    @Override
    public List<UserTaskAnswerChoice> getAnswer(MultipleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId) {
        List<Integer> correctChoiceIds = ((MultipleChoiceAnswer) task.getAnswer()).getChoiceIds();

        return task.getChoices().stream()
            .map(TaskChoice::getId)
            .map(choiceId -> userTaskAnswerChoice(
                    userTaskAnswerId
                    , choiceId
                    , userAnswer.getChoiceIds().contains(choiceId),
                    isCorrectUserChoice(choiceId, correctChoiceIds, userAnswer.getChoiceIds())
                )
            )
            .collect(Collectors.toList());
    }

    @Override
    public UserTaskAnswerChoice getAnswer(SingleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId) {
        Integer userChoiceId = userAnswer.getChoiceId();
        UserTaskAnswerChoice userTaskAnswerChoice = userTaskAnswerChoice(userTaskAnswerId, userChoiceId,
            true, ((SingleChoiceAnswer) task.getAnswer()).getChoiceId().equals(userChoiceId));

        save(userTaskAnswerChoice);

        return userTaskAnswerChoice;
    }

    private boolean isCorrectUserChoice(Integer choiceId, List<Integer> correctChoiceIds, List<Integer> userChoiceIds) {
        return correctChoiceIds.contains(choiceId) == userChoiceIds.contains(choiceId);
    }

    private UserTaskAnswerChoice userTaskAnswerChoice(Integer userTaskAnswerId, Integer choiceId, boolean isMarked,
                                                      boolean isCorrect) {
        UserTaskAnswerChoice userTaskAnswerChoice = new UserTaskAnswerChoice();
        userTaskAnswerChoice.setUserTaskAnswerId(userTaskAnswerId);
        userTaskAnswerChoice.setTaskChoiceId(choiceId);
        userTaskAnswerChoice.setIsCorrect(isCorrect);
        userTaskAnswerChoice.setIsMarked(isMarked);
        return userTaskAnswerChoice;
    }
}
