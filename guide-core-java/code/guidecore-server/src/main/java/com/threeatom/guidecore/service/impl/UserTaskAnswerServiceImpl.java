
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.mapper.UserTaskAnswerMapper;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import org.springframework.stereotype.Service;

@Service
public class UserTaskAnswerServiceImpl extends ServiceImpl<UserTaskAnswerMapper, UserTaskAnswer>
        implements UserTaskAnswerService {
}
