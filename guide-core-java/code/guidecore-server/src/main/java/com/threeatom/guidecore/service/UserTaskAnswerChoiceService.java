
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import java.util.List;

public interface UserTaskAnswerChoiceService extends IService<UserTaskAnswerChoice> {
    List<UserTaskAnswerChoice> createAnswer(MultipleChoiceAnswer answer, Task task, Integer userTaskAnswerId);

    UserTaskAnswerChoice createAnswer(SingleChoiceAnswer answer, Task task, Integer userTaskAnswerId);
}
