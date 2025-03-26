package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.Feedback;
import java.time.OffsetDateTime;
import java.util.List;

public interface FeedbackMapper extends BaseMapper<Feedback> {
    List<Feedback> userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, Integer userId);
}
