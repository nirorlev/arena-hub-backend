package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;

public interface UserTaskAnswerChoiceFillInBlankService extends IService<UserTaskAnswerChoiceFillInBlank> {
    void createAnswer(FillInTheBlankAnswer userAnswer, FillInTheBlankAnswer taskAnswer, Integer userTaskAnswerId);
}
