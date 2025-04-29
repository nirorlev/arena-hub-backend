
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import java.util.List;

public interface UserTaskAnswerReviewService extends IService<UserTaskAnswerReview> {
    void createMultipleChoiceAnswerReview(List<UserTaskAnswerChoice> userTaskAnswerChoices, Integer userTaskAnswerId);

    void createPairingAnswerReview(List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings,
                                   PairingProperties pairingProperties, Integer userTaskAnswerId);

    void createSingleChoiceAnswerReview(UserTaskAnswerChoice userTaskAnswerChoice, Integer userTaskAnswerId);

    void createFillInTheBlankAnswerReview(List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks,
                                          Integer userTaskAnswerId);

    UserTaskAnswerReview getMultipleChoiceAnswerReview(List<UserTaskAnswerChoice> userTaskAnswerChoices,
                                                       Integer userTaskAnswerId);

    UserTaskAnswerReview getPairingAnswerReview(List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings,
                                                PairingProperties pairingProperties,
                                                Integer userTaskAnswerId);

    UserTaskAnswerReview getSingleChoiceAnswerReview(UserTaskAnswerChoice userTaskAnswerChoice,
                                                     Integer userTaskAnswerId);

    UserTaskAnswerReview getFillInTheBlankAnswerReview(
        List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks,
        Integer userTaskAnswerId);
}
