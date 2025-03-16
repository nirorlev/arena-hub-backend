
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.UserTaskAnswerChoicePairing;
import com.threeatom.guidecore.mapper.UserTaskAnswerChoicePairingMapper;
import com.threeatom.guidecore.service.UserTaskAnswerChoicePairingService;
import org.springframework.stereotype.Service;

@Service
public class UserTaskAnswerChoicePairingServiceImpl extends ServiceImpl<UserTaskAnswerChoicePairingMapper, UserTaskAnswerChoicePairing>
        implements UserTaskAnswerChoicePairingService {
}
