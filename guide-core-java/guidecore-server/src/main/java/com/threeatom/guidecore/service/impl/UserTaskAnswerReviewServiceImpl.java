
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.entity.UserTaskAnswerChoiceFillInBlank;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import com.threeatom.guidecore.mapper.UserTaskAnswerReviewMapper;
import com.threeatom.guidecore.service.UserTaskAnswerReviewService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTaskAnswerReviewServiceImpl extends ServiceImpl<UserTaskAnswerReviewMapper, UserTaskAnswerReview>
    implements UserTaskAnswerReviewService {

    @Override
    @Transactional
    public void createMultipleChoiceAnswerReview(List<UserTaskAnswerChoice> userTaskAnswerChoices,
                                                 Integer userTaskAnswerId) {

        if (userTaskAnswerChoices.stream().allMatch(UserTaskAnswerChoice::getIsCorrect)) {
            createTaskAnswerReview(userTaskAnswerId, 100);
            return;
        }

        createTaskAnswerReview(userTaskAnswerId, 0);
    }

    @Override
    public void createPairingAnswerReview(List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings,
                                          PairingProperties pairingProperties,
                                          Integer userTaskAnswerId) {
        long correctPairingsCount = userTaskAnswerChoicePairings.stream()
            .filter(UserTaskAnswerChoicePairing::getIsCorrect)
            .count();

        double score = correctPairingsCount >= pairingProperties.getMinRequiredPairs() ? 100 : 0;
        createTaskAnswerReview(userTaskAnswerId, score);
    }

    @Override
    public void createSingleChoiceAnswerReview(UserTaskAnswerChoice userTaskAnswerChoice, Integer userTaskAnswerId) {
        double score = userTaskAnswerChoice.getIsCorrect() ? 100 : 0;
        createTaskAnswerReview(userTaskAnswerId, score);
    }

    @Override
    public void createFillInTheBlankAnswerReview(List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks,
                                                 Integer userTaskAnswerId) {
        boolean areAllFillingsCorrect = userTaskAnswerChoiceFillInBlanks.stream()
            .allMatch(UserTaskAnswerChoiceFillInBlank::getIsCorrect);
        double score = areAllFillingsCorrect ? 100.0 : 0;

        createTaskAnswerReview(userTaskAnswerId, score);
    }

    private void createTaskAnswerReview(Integer userTaskAnswerId, double score) {
        UserTaskAnswerReview userTaskAnswerReview = new UserTaskAnswerReview();
        userTaskAnswerReview.setUserTaskAnswerId(userTaskAnswerId);
        userTaskAnswerReview.setScore(score);
        save(userTaskAnswerReview);
    }
}
