package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.FeedbackAverage;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FeedbackAverageService extends IService<FeedbackAverage> {

    FeedbackAverage createFeedbackAverage(FeedbackItemType itemType, Integer itemId, List<Feedback> feedbacks);

    FeedbackAverage updateFeedbackAverage(FeedbackAverage feedbackAverage, int rating, int newCount);

    Optional<FeedbackAverage> findByItemTypeAndId(FeedbackItemType itemType, Integer itemId);

    FeedbackAverage getByItemTypeAndId(FeedbackItemType itemType, Integer itemId);

    Map<FeedbackItemType, Map<Integer, FeedbackAverage>> getByFeedbackItemTypeAndItemId(
        Map<FeedbackItemType, List<Integer>> feedbackItemTypeToItemIds);
}
