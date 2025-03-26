package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import javax.validation.Valid;

public interface FeedbackFacade {
    FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                               com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto, PortalUser portalUser);
}
