package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.response.FeedbackAverageDto;
import com.threeatom.guidecore.dto.response.FeedbackSummaryDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.FeedbackAverage;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.mapper.FeedbackMapper;
import com.threeatom.guidecore.mapping.FeedbackMapping;
import com.threeatom.guidecore.mapping.UserMapping;
import com.threeatom.guidecore.service.FeedbackAverageService;
import com.threeatom.guidecore.service.FeedbackService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    private final FeedbackMapping feedbackMapping;
    private final UserMapping userMapping;
    private final FeedbackAverageService feedbackAverageService;

    @Override
    @Transactional
    public FeedbackAverageDto createOrUpdateFeedback(FeedbackItemType itemType, Integer itemId,
                                                     com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                                     PortalUser portalUser) {
        List<Feedback> existingFeedbacks = findByItemTypeAndId(itemType, itemId);
        Optional<Feedback> userExistingFeedback = findUserFeedback(existingFeedbacks, portalUser.getUserId());
        if (userExistingFeedback.isEmpty()) {
            Feedback feedback = createFeedback(itemType, itemId, feedbackDto, portalUser);
            FeedbackAverage feedbackAverage =
                createUpdateFeedbackAverage(itemType, itemId, existingFeedbacks, feedback);
            return feedbackMapping.map(feedback, feedbackAverage);
        }

        return patchFeedback(userExistingFeedback.get(), feedbackDto);
    }

    @Override
    public Feedback getById(Long feedbackId) {
        return findById(feedbackId).orElseThrow(() -> {
            log.error("Feedback not found by requested id: {}", feedbackId);
            return new ResourceNotFoundException("Feedback not found by requested id");
        });
    }

    @Override
    @Transactional
    public FeedbackAverageDto updateFeedback(Feedback feedback,
                                             com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        Integer previousRating = feedback.getRating();
        feedbackMapping.mapUpdate(feedback, feedbackDto);
        updateById(feedback);

        FeedbackAverage feedbackAverage = updateFeedbackAverage(feedback, previousRating);
        return feedbackMapping.map(feedback, feedbackAverage);
    }

    @Override
    public FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> userFeedbacks = baseMapper.userFeedbacks(startDate, endDate, portalUser.getUserId());
        Map<FeedbackItemType, Map<Integer, FeedbackAverage>> feedbackTypeToItemIdAndAverage =
            feedbackAverageService.getByFeedbackItemTypeAndItemId(getFeedbackItemTypeToItemIds(userFeedbacks));

        return feedbacksDto(userFeedbacks, feedbackTypeToItemIdAndAverage);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, portalUser.getUserId(), itemType, itemId);
        Map<FeedbackItemType, Map<Integer, FeedbackAverage>> feedbackTypeToItemIdAndAverage =
            feedbackAverageService.getByFeedbackItemTypeAndItemId(getFeedbackItemTypeToItemIds(feedbacks));

        return feedbacksDto(feedbacks, feedbackTypeToItemIdAndAverage);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, null, itemType, itemId);
        Map<FeedbackItemType, Map<Integer, FeedbackAverage>> feedbackTypeToItemIdAndAverage =
            feedbackAverageService.getByFeedbackItemTypeAndItemId(getFeedbackItemTypeToItemIds(feedbacks));

        return feedbacksDto(feedbacks, feedbackTypeToItemIdAndAverage);
    }

    @Override
    @Transactional
    public FeedbackAverageDto deleteFeedback(Feedback feedback) {
        removeById(feedback.getId());

        FeedbackAverage feedbackAverage = deleteFromFeedbackAverage(feedback);
        if (feedbackAverage == null) {
            return new FeedbackAverageDto();
        }

        return feedbackMapping.map(feedbackAverage);
    }

    @Override
    @Transactional
    public FeedbackAverageDto patchFeedback(Feedback feedback,
                                            com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        int previousRating = feedback.getRating();
        feedbackMapping.mapPatch(feedback, feedbackDto);
        updateById(feedback);

        FeedbackAverage feedbackAverage = updateFeedbackAverage(feedback, previousRating);
        return feedbackMapping.map(feedback, feedbackAverage);
    }

    private Optional<Feedback> findUserFeedback(List<Feedback> feedbacks, Integer userId) {
        return feedbacks.stream()
            .filter(feedback -> feedback.getUserId().equals(userId))
            .findFirst();
    }

    private Feedback createFeedback(FeedbackItemType itemType, Integer itemId,
                                    com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                    PortalUser portalUser) {
        Feedback feedback = feedbackMapping.map(feedbackDto, itemType, itemId, portalUser.getUserId());
        save(feedback);
        return feedback;
    }

    private FeedbackAverage createUpdateFeedbackAverage(FeedbackItemType itemType, Integer itemId,
                                                        List<Feedback> existingFeedbacks, Feedback feedback) {
        Optional<FeedbackAverage> feedbackAverageOptional =
            feedbackAverageService.findByItemTypeAndId(itemType, itemId);
        if (feedbackAverageOptional.isEmpty()) {
            List<Feedback> allFeedbacks = new ArrayList<>(existingFeedbacks);
            allFeedbacks.add(feedback);
            return feedbackAverageService.createFeedbackAverage(itemType, itemId, allFeedbacks);
        }

        FeedbackAverage feedbackAverage = feedbackAverageOptional.get();
        return feedbackAverageService.addRatingToFeedbackAverage(feedbackAverage, feedback.getRating());
    }

    private FeedbackAverage updateFeedbackAverage(Feedback feedback, int previousRating) {
        FeedbackAverage feedbackAverage =
            feedbackAverageService.getByItemTypeAndId(feedback.getItemType(), feedback.getItemId());
        return feedbackAverageService.updateRatingInFeedbackAverage(feedbackAverage, feedback.getRating(), previousRating);
    }

    private FeedbackAverage deleteFromFeedbackAverage(Feedback feedback) {
        FeedbackAverage feedbackAverage =
            feedbackAverageService.getByItemTypeAndId(feedback.getItemType(), feedback.getItemId());
        return feedbackAverageService.deleteRatingFromFeedbackAverage(feedbackAverage, feedback.getRating());
    }

    private Optional<Feedback> findById(Long id) {
        return Optional.ofNullable(super.getById(id));
    }

    private List<Feedback> findByItemTypeAndId(FeedbackItemType feedbackItemType, Integer itemId) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_type", feedbackItemType);
        queryWrapper.eq("item_id", itemId);
        return list(queryWrapper);
    }

    private Map<FeedbackItemType, List<Integer>> getFeedbackItemTypeToItemIds(List<Feedback> feedbacks) {
        return feedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getItemType,
                Collectors.mapping(Feedback::getItemId, Collectors.toList())));
    }

    private Map<Integer, List<Feedback>> itemIdToFeedbacks(List<Feedback> feedbackForItemIds) {
        return feedbackForItemIds.stream()
            .collect(Collectors.groupingBy(Feedback::getItemId));
    }

    private FeedbacksDto feedbacksDto(List<Feedback> feedbacks,
                                      Map<FeedbackItemType, Map<Integer, FeedbackAverage>> feedbackTypeToItemIdAndAverage) {
        FeedbacksDto feedbacksDto = new FeedbacksDto();
        feedbacksDto.setUsers(notAnonymousUserIdToUserDetails(feedbacks));
        feedbacksDto.setFeedbackTypes(feedbackItemTypeToItemFeedbacks(feedbacks, feedbackTypeToItemIdAndAverage));
        return feedbacksDto;
    }

    private Map<String, UserDetailsDto> notAnonymousUserIdToUserDetails(List<Feedback> feedbacks) {
        List<GcUser> uniqueNotAnonymousUsers = uniqueNotAnonymousUsers(feedbacks);
        List<UserDetailsDto> userDetails = userMapping.map(uniqueNotAnonymousUsers);
        return userDetails.stream()
            .collect(Collectors.toMap(userDetailsDto -> String.valueOf(userDetailsDto.getId()), Function.identity()));
    }

    private List<GcUser> uniqueNotAnonymousUsers(List<Feedback> feedbacks) {
        Set<Integer> userIds = new HashSet<>();
        return feedbacks.stream()
            .filter(feedback -> !feedback.getAnonymous())
            .map(Feedback::getUser)
            .filter(user -> userIds.add(user.getId()))
            .collect(Collectors.toList());
    }

    private EnumMap<FeedbackItemType, Map<String, FeedbackSummaryDto>> feedbackItemTypeToItemFeedbacks(
        List<Feedback> feedbacks, Map<FeedbackItemType, Map<Integer, FeedbackAverage>> feedbackTypeToItemIdAndAverage) {

        return feedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getItemType, () -> new EnumMap<>(FeedbackItemType.class),
                Collectors.toList()))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Map<Integer, FeedbackAverage> itemIdToFeedbackAverage =
                        feedbackTypeToItemIdAndAverage.getOrDefault(entry.getKey(), Map.of());
                    return convertToItemIdToFeedbackSummary(itemIdToFeedbackAverage, entry.getValue());
                },
                (exiting1, exiting2) -> exiting1,
                () -> new EnumMap<>(FeedbackItemType.class)
            ));
    }

    private Map<String, FeedbackSummaryDto> convertToItemIdToFeedbackSummary(
        Map<Integer, FeedbackAverage> itemIdToFeedbackAverage, List<Feedback> itemTypeFeedbacks) {

        return itemIdToFeedbacks(itemTypeFeedbacks)
            .entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()),
                itemFeedbacksEntry -> feedbackSummaryDto(itemIdToFeedbackAverage, itemFeedbacksEntry.getValue(),
                    itemFeedbacksEntry.getKey())));
    }

    private FeedbackSummaryDto feedbackSummaryDto(Map<Integer, FeedbackAverage> itemIdToAverageFeedback,
                                                  List<Feedback> itemFeedbacks, Integer itemId) {
        FeedbackAverage feedbackAverage = itemIdToAverageFeedback.get(itemId);
        FeedbackSummaryDto feedbackSummaryDto = new FeedbackSummaryDto();
        feedbackSummaryDto.setFeedbacksCount(feedbackAverage.getFeedbacksCount());
        feedbackSummaryDto.setAverageRating(feedbackAverage.getAverageRating());
        feedbackSummaryDto.setFeedbacks(feedbackMapping.map(itemFeedbacks));
        return feedbackSummaryDto;
    }
}
