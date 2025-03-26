package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.time.OffsetDateTime;

public interface FeedbackFacade {
    FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                               com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto, PortalUser portalUser);

    FeedbackDto updateFeedback(Long feedbackId, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                               PortalUser portalUser);

    FeedbackDto updateLatestOrCreateFeedback(FeedbackItemType itemType, Integer itemId,
                                             com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                             PortalUser portalUser);

    void deleteFeedback(Long feedbackId, PortalUser portalUser);

    FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser);

    FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate, OffsetDateTime endDate,
                           String users, PortalUser portalUser);
}
