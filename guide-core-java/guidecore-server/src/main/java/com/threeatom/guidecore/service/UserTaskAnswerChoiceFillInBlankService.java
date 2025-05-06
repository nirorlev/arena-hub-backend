package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import java.util.List;

public interface UserTaskAnswerChoiceFillInBlankService extends IService<UserTaskAnswerChoiceFillInBlank> {
    List<UserTaskAnswerChoiceFillInBlank> createAnswer(FillInTheBlankAnswer userAnswer, FillInTheBlankAnswer taskAnswer,
                                                       Integer userTaskAnswerId);

    List<UserTaskAnswerChoiceFillInBlank> getAnswer(FillInTheBlankAnswer userAnswer, FillInTheBlankAnswer taskAnswer,
                                                    Integer userTaskAnswerId);
}
