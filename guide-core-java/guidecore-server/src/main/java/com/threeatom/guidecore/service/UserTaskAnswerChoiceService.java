package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import java.util.List;

public interface UserTaskAnswerChoiceService extends IService<UserTaskAnswerChoice> {
    List<UserTaskAnswerChoice> createAnswer(MultipleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId);

    UserTaskAnswerChoice createAnswer(SingleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId);

    List<UserTaskAnswerChoice> getAnswer(MultipleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId);

    UserTaskAnswerChoice getAnswer(SingleChoiceAnswer userAnswer, Task task, Integer userTaskAnswerId);
}
