package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.FeedbackAverage;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.mapper.FeedbackAverageMapper;
import com.threeatom.guidecore.service.FeedbackAverageService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackAverageServiceImpl extends ServiceImpl<FeedbackAverageMapper, FeedbackAverage>
    implements FeedbackAverageService {

    @Transactional
    @Override
    public FeedbackAverage createFeedbackAverage(FeedbackItemType itemType, Integer itemId, List<Feedback> feedbacks) {
        FeedbackAverage feedbackAverage = feedbackAverage(itemType, itemId, feedbacks);
        save(feedbackAverage);
        return feedbackAverage;
    }

    @Override
    public FeedbackAverage addRatingToFeedbackAverage(FeedbackAverage feedbackAverage, int rating) {
        Integer feedbacksCount = feedbackAverage.getFeedbacksCount();
        return updateFeedbackAverage(feedbackAverage, feedbacksCount, rating, feedbacksCount + 1);
    }

    @Override
    public FeedbackAverage updateRatingInFeedbackAverage(FeedbackAverage feedbackAverage, int rating,
                                                         int previousRating) {
        Integer feedbacksCount = feedbackAverage.getFeedbacksCount();
        return updateFeedbackAverage(feedbackAverage, feedbacksCount, -previousRating + rating, feedbacksCount);
    }

    @Override
    public FeedbackAverage deleteRatingFromFeedbackAverage(FeedbackAverage feedbackAverage, int rating) {
        Integer feedbacksCount = feedbackAverage.getFeedbacksCount();
        if (feedbacksCount == 1) {
            removeById(feedbackAverage.getId());
            return null;
        }

        return updateFeedbackAverage(feedbackAverage, feedbacksCount, rating, feedbacksCount - 1);
    }

    private FeedbackAverage updateFeedbackAverage(FeedbackAverage feedbackAverage, Integer feedbacksCount, int rating,
                                                  int newFeedbacksCount) {
        feedbackAverage.setAverageRating(
            recalculateAverage(feedbackAverage.getAverageRating(), feedbacksCount, rating, newFeedbacksCount));
        feedbackAverage.setFeedbacksCount(newFeedbacksCount);
        feedbackAverage.setUpdatedTime(OffsetDateTime.now());

        updateById(feedbackAverage);
        return feedbackAverage;
    }

    @Override
    public Optional<FeedbackAverage> findByItemTypeAndId(FeedbackItemType itemType, Integer itemId) {
        QueryWrapper<FeedbackAverage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_type", itemType);
        queryWrapper.eq("item_id", itemId);
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    public FeedbackAverage getByItemTypeAndId(FeedbackItemType itemType, Integer itemId) {
        return findByItemTypeAndId(itemType, itemId).orElseThrow(() -> {
            log.error("Feedback average not found for itemType: {}, itemId: {}", itemType, itemId);
            return new ResourceNotFoundException("Feedback average not found");
        });
    }

    @Override
    public Map<FeedbackItemType, Map<Integer, FeedbackAverage>> getByFeedbackItemTypeAndItemId(
        Map<FeedbackItemType, List<Integer>> feedbackItemTypeToItemIds) {

        return feedbackItemTypeToItemIds.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<FeedbackAverage> feedbackAverages = getFeedbackAverages(entry.getKey(), entry.getValue());
                    return feedbackAverages.stream()
                        .collect(Collectors.toMap(FeedbackAverage::getItemId, Function.identity()));
                }
            ));
    }

    private List<FeedbackAverage> getFeedbackAverages(FeedbackItemType itemType, List<Integer> itemIds) {
        QueryWrapper<FeedbackAverage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_type", itemType);
        queryWrapper.in("item_id", itemIds);

        return list(queryWrapper);
    }

    private FeedbackAverage feedbackAverage(FeedbackItemType itemType, Integer itemId, List<Feedback> feedbacks) {
        FeedbackAverage feedbackAverage = new FeedbackAverage();
        feedbackAverage.setItemId(itemId);
        feedbackAverage.setItemType(itemType);
        feedbackAverage.setFeedbacksCount(feedbacks.size());
        feedbackAverage.setAverageRating(averageRating(feedbacks));
        return feedbackAverage;
    }

    public double recalculateAverage(double existingAverage, int existingCount, int rating, int newCount) {
        return (existingAverage * existingCount + rating) / newCount;
    }

    private double averageRating(List<Feedback> feedbacks) {
        return feedbacks.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0);
    }
}
