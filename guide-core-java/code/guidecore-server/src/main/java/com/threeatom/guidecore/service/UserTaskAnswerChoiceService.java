
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;

public interface UserTaskAnswerChoiceService extends IService<UserTaskAnswerChoice> {
    void createAnswer(MultipleChoiceAnswer answer, Task task, Integer userTaskAnswerId);

    void createAnswer(SingleChoiceAnswer answer, Task task, Integer userTaskAnswerId);
}
