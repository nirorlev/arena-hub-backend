package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.FeedbackDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface FeedbackService extends IService<Feedback> {
    com.threeatom.guidecore.dto.response.FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                                                                    FeedbackDto feedbackDto, PortalUser portalUser);

    Feedback getById(Long feedbackId);

    Optional<Feedback> findLatest(FeedbackItemType feedbackItemType, Integer itemId, PortalUser portalUser);

    com.threeatom.guidecore.dto.response.FeedbackDto updateFeedback(Feedback feedback, FeedbackDto feedbackDto);

    FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser);
}
