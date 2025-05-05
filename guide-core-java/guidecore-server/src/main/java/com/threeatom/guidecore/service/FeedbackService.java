package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.FeedbackDto;
import com.threeatom.guidecore.dto.response.FeedbackAverageDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.time.OffsetDateTime;

public interface FeedbackService extends IService<Feedback> {
    FeedbackAverageDto createOrUpdateFeedback(FeedbackItemType itemType, Integer itemId, FeedbackDto feedbackDto,
                                              PortalUser portalUser);

    Feedback getById(Long feedbackId);

    FeedbackAverageDto updateFeedback(Feedback feedback, FeedbackDto feedbackDto);

    FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser);

    FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate, OffsetDateTime endDate,
                           PortalUser portalUser);

    FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate, OffsetDateTime endDate);

    FeedbackAverageDto deleteFeedback(Feedback feedback);

    FeedbackAverageDto patchFeedback(Feedback feedback, FeedbackDto feedbackDto);
}
