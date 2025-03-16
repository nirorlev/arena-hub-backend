
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.UserTaskAnswerChoice;
import com.threeatom.guidecore.mapper.UserTaskAnswerChoiceMapper;
import com.threeatom.guidecore.service.UserTaskAnswerChoiceService;
import org.springframework.stereotype.Service;

@Service
public class UserTaskAnswerChoiceServiceImpl extends ServiceImpl<UserTaskAnswerChoiceMapper, UserTaskAnswerChoice>
        implements UserTaskAnswerChoiceService {
}
