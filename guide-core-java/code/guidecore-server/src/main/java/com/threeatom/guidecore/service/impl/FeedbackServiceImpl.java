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

    @Override
    @Transactional
    public FeedbackDto createOrUpdateFeedback(FeedbackItemType itemType, Integer itemId,
                                              com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                              PortalUser portalUser) {
        Optional<Feedback> existingFeedback = findByItemTypeItemAndUserId(itemType, itemId, portalUser.getUserId());

        if (existingFeedback.isEmpty()) {
            Feedback feedback = createFeedback(itemType, itemId, feedbackDto, portalUser);
            return feedbackMapping.map(feedback);
        }

        return patchFeedback(existingFeedback.get(), feedbackDto);
    }

    private Feedback createFeedback(FeedbackItemType itemType, Integer itemId,
                                    com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                    PortalUser portalUser) {
        Feedback feedback = feedbackMapping.map(feedbackDto, itemType, itemId, portalUser.getUserId());
        save(feedback);
        return feedback;
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
    public FeedbackDto updateFeedback(Feedback feedback, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        feedbackMapping.mapUpdate(feedback, feedbackDto);
        updateById(feedback);

        return feedbackMapping.map(feedback);
    }

    @Override
    public FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> userFeedbacks = baseMapper.userFeedbacks(startDate, endDate, portalUser.getUserId());

        return feedbacksDto(userFeedbacks);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate, PortalUser portalUser) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, portalUser.getUserId(), itemType, itemId);

        return feedbacksDto(feedbacks);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate) {
        List<Feedback> feedbacks = baseMapper.feedbacks(startDate, endDate, null, itemType, itemId);

        return feedbacksDto(feedbacks);
    }

    @Override
    public FeedbackDto patchFeedback(Feedback feedback, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        feedbackMapping.mapPatch(feedback, feedbackDto);
        updateById(feedback);

        return feedbackMapping.map(feedback);
    }

    private Optional<Feedback> findById(Long id) {
        return Optional.ofNullable(super.getById(id));
    }

    private Optional<Feedback> findByItemTypeItemAndUserId(FeedbackItemType feedbackItemType, Integer itemId,
                                                           Integer userId) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_type", feedbackItemType);
        queryWrapper.eq("item_id", itemId);
        queryWrapper.eq("user_id", userId);
        return Optional.ofNullable(getOne(queryWrapper));
    }

    private Map<Integer, List<Feedback>> itemIdToFeedbacks(List<Feedback> feedbackForItemIds) {
        return feedbackForItemIds.stream()
            .collect(Collectors.groupingBy(Feedback::getItemId));
    }

    private FeedbacksDto feedbacksDto(List<Feedback> feedbacks) {
        FeedbacksDto feedbacksDto = new FeedbacksDto();
        feedbacksDto.setUsers(notAnonymousUserIdToUserDetails(feedbacks));
        feedbacksDto.setFeedbackTypes(feedbackItemTypeToItemFeedbacks(feedbacks));
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
        List<Feedback> feedbacks) {

        return feedbacks.stream()
            .collect(Collectors.groupingBy(Feedback::getItemType, () -> new EnumMap<>(FeedbackItemType.class),
                Collectors.toList()))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                this::convertToItemIdToFeedbackSummary,
                (existing1, existing2) -> existing1,
                () -> new EnumMap<>(FeedbackItemType.class)
            ));
    }

    private Map<String, FeedbackSummaryDto> convertToItemIdToFeedbackSummary(
        Map.Entry<FeedbackItemType, List<Feedback>> entry) {

        return itemIdToFeedbacks(entry.getValue())
            .entrySet().stream()
            .collect(Collectors.toMap(itemFeedbackEntry -> String.valueOf(itemFeedbackEntry.getKey()),
                itemFeedbackEntry -> feedbackSummaryDto(itemFeedbackEntry.getValue())));
    }

    private FeedbackSummaryDto feedbackSummaryDto(List<Feedback> itemFeedbacks) {
        FeedbackSummaryDto feedbackSummaryDto = new FeedbackSummaryDto();
        feedbackSummaryDto.setFeedbacks(feedbackMapping.map(itemFeedbacks));
        return feedbackSummaryDto;
    }
}
