
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.mapper.UserTaskAnswerChoicePairingMapper;
import com.threeatom.guidecore.service.UserTaskAnswerChoicePairingService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserTaskAnswerChoicePairingServiceImpl
    extends ServiceImpl<UserTaskAnswerChoicePairingMapper, UserTaskAnswerChoicePairing>
    implements UserTaskAnswerChoicePairingService {

    @Override
    @Transactional
    public List<UserTaskAnswerChoicePairing> createAnswer(PairingAnswer userChoicePairings,
                                                          PairingAnswer taskAnswerChoicePairings,
                                                          PairingProperties pairingProperties,
                                                          Integer userTaskAnswerId) {
        List<List<Integer>> userAnswerChoiceIds = userChoicePairings.getChoiceIds();
        verifyChoicePairsValid(userAnswerChoiceIds, pairingProperties);

        List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings = userAnswerChoiceIds.stream()
            .map(choices -> userTaskAnswerChoicePairing(userTaskAnswerId, choices,
                taskAnswerChoicePairings.getChoiceIds()))
            .collect(Collectors.toList());

        saveBatch(userTaskAnswerChoicePairings);
        return userTaskAnswerChoicePairings;
    }

    private UserTaskAnswerChoicePairing userTaskAnswerChoicePairing(Integer userTaskAnswerId, List<Integer> choices,
                                                                    List<List<Integer>> taskChoicePairings) {
        UserTaskAnswerChoicePairing userTaskAnswerChoicePairing = new UserTaskAnswerChoicePairing();
        userTaskAnswerChoicePairing.setUserTaskAnswerId(userTaskAnswerId);
        userTaskAnswerChoicePairing.setTaskChoiceId(choices.get(0));
        userTaskAnswerChoicePairing.setPairedChoiceId(choices.get(1));
        userTaskAnswerChoicePairing.setIsCorrect(taskChoicePairings.contains(choices));
        return userTaskAnswerChoicePairing;
    }

    private void verifyChoicePairsValid(List<List<Integer>> choicePairings, PairingProperties pairingProperties) {
        boolean invalidChoicesExists = choicePairings.stream()
            .anyMatch(choices -> choices.size() != 2);

        if (invalidChoicesExists) {
            log.error("Invalid choices for pairing answer passed '{}'", choicePairings);
            throw new ValidationException("Invalid choices for pairing answer passed");
        }
    }
}
