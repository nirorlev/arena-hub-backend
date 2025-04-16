package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import com.threeatom.guidecore.mapper.UserTaskAnswerChoiceFillInBlankMapper;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceFillInBlankService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTaskAnswerChoiceFillInBlankServiceImpl
    extends ServiceImpl<UserTaskAnswerChoiceFillInBlankMapper, UserTaskAnswerChoiceFillInBlank>
    implements UserTaskAnswerChoiceFillInBlankService {

    @Override
    @Transactional
    public List<UserTaskAnswerChoiceFillInBlank> createAnswer(FillInTheBlankAnswer userAnswer,
                                                              FillInTheBlankAnswer taskAnswer,
                                                              Integer userTaskAnswerId) {
        Map<String, Integer> taskKeywordToAnswer = taskAnswer.getKeywordToAnswer();
        Map<String, Integer> userKeywordToAnswer = userAnswer.getKeywordToAnswer();

        List<UserTaskAnswerChoiceFillInBlank> userTaskAnswers = userKeywordToAnswer.entrySet().stream()
            .map(entry -> userTaskAnswerChoiceFillInBlank(userTaskAnswerId, entry.getKey(), entry.getValue(),
                isCorrect(entry.getKey(), entry.getValue(), taskKeywordToAnswer)))
            .collect(Collectors.toList());

        saveBatch(userTaskAnswers);
        return userTaskAnswers;
    }

    private boolean isCorrect(String key, Integer choiceId, Map<String, Integer> keywordToAnswer) {
        return keywordToAnswer.containsKey(key) && keywordToAnswer.get(key).equals(choiceId);
    }

    private UserTaskAnswerChoiceFillInBlank userTaskAnswerChoiceFillInBlank(Integer userTaskAnswerId, String key,
                                                                            int taskChoiceId, boolean isCorrect) {
        UserTaskAnswerChoiceFillInBlank userTaskAnswerChoiceFillInBlank = new UserTaskAnswerChoiceFillInBlank();
        userTaskAnswerChoiceFillInBlank.setKey(key);
        userTaskAnswerChoiceFillInBlank.setTaskChoiceId(taskChoiceId);
        userTaskAnswerChoiceFillInBlank.setUserTaskAnswerId(userTaskAnswerId);
        userTaskAnswerChoiceFillInBlank.setIsCorrect(isCorrect);
        return userTaskAnswerChoiceFillInBlank;
    }
}
