package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.FeedbackDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;

public interface FeedbackService extends IService<Feedback> {
    com.threeatom.guidecore.dto.response.FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                                                                    FeedbackDto feedbackDto, PortalUser portalUser);

    Feedback getById(Integer feedbackId);

    com.threeatom.guidecore.dto.response.FeedbackDto updateFeedback(Feedback feedback, FeedbackDto feedbackDto);
}
