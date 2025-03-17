
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import com.threeatom.guidecore.mapper.UserTaskAnswerReviewMapper;
import com.threeatom.guidecore.service.UserTaskAnswerReviewService;
import org.springframework.stereotype.Service;

@Service
public class UserTaskAnswerReviewServiceImpl extends ServiceImpl<UserTaskAnswerReviewMapper, UserTaskAnswerReview>
    implements UserTaskAnswerReviewService {
}
