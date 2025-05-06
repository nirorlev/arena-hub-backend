
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import java.util.List;

public interface UserTaskAnswerChoicePairingService extends IService<UserTaskAnswerChoicePairing> {
    List<UserTaskAnswerChoicePairing> createAnswer(PairingAnswer userChoicePairings, PairingAnswer taskAnswerChoicePairings,
                                                   PairingProperties pairingProperties, Integer userTaskAnswerId);

    List<UserTaskAnswerChoicePairing> getAnswer(PairingAnswer userChoicePairings, PairingAnswer taskAnswerChoicePairings,
                                                   PairingProperties pairingProperties, Integer userTaskAnswerId);
}
