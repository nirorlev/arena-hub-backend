package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.dto.response.FeedbackSummaryDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.mapper.FeedbackMapper;
import com.threeatom.guidecore.mapping.FeedbackMapping;
import com.threeatom.guidecore.mapping.UserMapping;
import com.threeatom.guidecore.service.FeedbackService;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    private final FeedbackMapping feedbackMapping;
    private final UserMapping userMapping;

    @Override
    @Transactional
    public FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                                      com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                      PortalUser portalUser) {
        Feedback feedback = feedbackMapping.map(feedbackDto, itemType, itemId, portalUser.getUserId());
        save(feedback);
        return feedbackMapping.map(feedback);
    }

    @Override
    public Feedback getById(Long feedbackId) {
        Feedback feedback = super.getById(feedbackId);
        if (feedback == null) {
            log.error("Feedback not found by requested id: {}", feedbackId);
            throw new ResourceNotFoundException("Feedback not found by requested id");
        }

        return feedback;
    }

    @Override
    @Transactional
    public FeedbackDto updateFeedback(Feedback feedback, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        feedbackMapping.mapUpdate(feedback, feedbackDto);
        updateById(feedback);

        return feedbackMapping.map(feedback);
    }

    @Override
    public FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> userFeedbacks = baseMapper.userFeedbacks(startDate, endDate, portalUser.getUserId());
        List<Feedback> feedbackForItemIds = getFeedbackByItemIds(getItemIds(userFeedbacks), startDate, endDate);
        Map<Integer, List<Feedback>> itemIdToFeedbacks = itemIdToFeedbacks(feedbackForItemIds);

        return feedbacksDto(userFeedbacks, itemIdToFeedbacks);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, portalUser.getUserId(), itemType, itemId);
        List<Feedback> feedbackForItemIds = getFeedbackByItemIds(List.of(itemId), startDate, endDate);
        Map<Integer, List<Feedback>> itemIdToFeedbacks = itemIdToFeedbacks(feedbackForItemIds);

        return feedbacksDto(feedbacks, itemIdToFeedbacks);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, null, itemType, itemId);
        List<Feedback> feedbackForItemIds = getFeedbackByItemIds(List.of(itemId), startDate, endDate);
        Map<Integer, List<Feedback>> itemIdToFeedbacks = itemIdToFeedbacks(feedbackForItemIds);

        return feedbacksDto(feedbacks, itemIdToFeedbacks);
    }

    @Override
    public FeedbackDto patchFeedback(Feedback feedback, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        feedbackMapping.mapPatch(feedback, feedbackDto);
        updateById(feedback);

        return feedbackMapping.map(feedback);
    }

    private List<Feedback> getFeedbackByItemIds(List<Integer> feedbackItemIds, OffsetDateTime startDate,
                                                OffsetDateTime endDate) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("item_id", feedbackItemIds);
        queryWrapper.ge("updated_time", startDate);
        queryWrapper.le("updated_time", endDate);
        return list(queryWrapper);
    }

    private List<Integer> getItemIds(List<Feedback> feedbacks) {
        return feedbacks.stream()
            .map(Feedback::getItemId)
            .collect(Collectors.toList());
    }

    private Map<Integer, List<Feedback>> itemIdToFeedbacks(List<Feedback> feedbackForItemIds) {
        return feedbackForItemIds.stream()
            .collect(Collectors.groupingBy(Feedback::getItemId));
    }

    private FeedbacksDto feedbacksDto(List<Feedback> feedbacks, Map<Integer, List<Feedback>> itemIdToFeedbacks) {
        FeedbacksDto feedbacksDto = new FeedbacksDto();
        feedbacksDto.setUsers(notAnonymousUserIdToUserDetails(feedbacks));
        feedbacksDto.setFeedbackTypes(feedbackItemTypeToItemFeedbacks(feedbacks, itemIdToFeedbacks));
        return feedbacksDto;
    }

    private Map<Integer, UserDetailsDto> notAnonymousUserIdToUserDetails(List<Feedback> feedbacks) {
        List<GcUser> uniqueNotAnonymousUsers = uniqueNotAnonymousUsers(feedbacks);
        List<UserDetailsDto> userDetails = userMapping.map(uniqueNotAnonymousUsers);
        return userDetails.stream()
            .collect(Collectors.toMap(UserDetailsDto::getId, Function.identity()));
    }

    private List<GcUser> uniqueNotAnonymousUsers(List<Feedback> feedbacks) {
        Set<Integer> userIds = new HashSet<>();
        return feedbacks.stream()
            .filter(feedback -> !feedback.getAnonymous())
            .map(Feedback::getUser)
            .filter(user -> userIds.add(user.getId()))
            .collect(Collectors.toList());
    }

    private EnumMap<FeedbackItemType, Map<Integer, FeedbackSummaryDto>> feedbackItemTypeToItemFeedbacks(
        List<Feedback> feedbacks,
        Map<Integer, List<Feedback>> itemIdToFeedbacks) {

        return feedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getItemType, () -> new EnumMap<>(FeedbackItemType.class),
                Collectors.toList()))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> convertToItemIdToFeedbackSummary(entry, itemIdToFeedbacks),
                (exiting1, exiting2) -> exiting1,
                () -> new EnumMap<>(FeedbackItemType.class)
            ));
    }

    private Map<Integer, FeedbackSummaryDto> convertToItemIdToFeedbackSummary(
        Map.Entry<FeedbackItemType, List<Feedback>> entry,
        Map<Integer, List<Feedback>> itemIdToFeedbacks) {

        return itemIdToFeedbacks(entry.getValue())
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                itemFeedback -> feedbackSummaryDto(itemIdToFeedbacks, itemFeedback.getValue(), itemFeedback.getKey())));
    }

    private FeedbackSummaryDto feedbackSummaryDto(Map<Integer, List<Feedback>> itemIdToFeedbacks,
                                                  List<Feedback> itemFeedbacks, Integer itemId) {
        FeedbackSummaryDto feedbackSummaryDto = new FeedbackSummaryDto();
        feedbackSummaryDto.setFeedbacksCount(itemIdFeedbacksCount(itemId, itemIdToFeedbacks));
        feedbackSummaryDto.setAverageRating(averageItemFeedbackRating(itemId, itemIdToFeedbacks));
        feedbackSummaryDto.setFeedbacks(feedbackMapping.map(itemFeedbacks));
        return feedbackSummaryDto;
    }

    private int itemIdFeedbacksCount(Integer itemId, Map<Integer, List<Feedback>> itemIdToFeedbacks) {
        List<Feedback> feedbacks = itemIdToFeedbacks.get(itemId);
        if (CollectionUtils.isEmpty(feedbacks)) {
            return 0;
        }

        return feedbacks.size();
    }

    private double averageItemFeedbackRating(Integer itemId, Map<Integer, List<Feedback>> itemIdToFeedbacks) {
        List<Feedback> feedbacks = itemIdToFeedbacks.get(itemId);
        if (CollectionUtils.isEmpty(feedbacks)) {
            return 0;
        }

        return feedbacks.stream()
            .mapToInt(Feedback::getRating)
            .average()
            .orElse(0);
    }
}
