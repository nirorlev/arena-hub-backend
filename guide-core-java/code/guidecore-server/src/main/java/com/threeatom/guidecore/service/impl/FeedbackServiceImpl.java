package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.mapper.FeedbackMapper;
import com.threeatom.guidecore.mapping.FeedbackMapping;
import com.threeatom.guidecore.service.FeedbackService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    private final FeedbackMapping feedbackMapping;

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
    public Optional<Feedback> findLatest(FeedbackItemType feedbackItemType, Integer itemId, PortalUser portalUser) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_type", feedbackItemType);
        queryWrapper.eq("item_id", itemId);
        queryWrapper.eq("user_id", portalUser.getUserId());
        queryWrapper.orderByDesc("created_time");
        queryWrapper.last("limit 1");

        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    @Transactional
    public FeedbackDto updateFeedback(Feedback feedback, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto) {
        feedbackMapping.mapUpdate(feedback, feedbackDto);
        updateById(feedback);

        return feedbackMapping.map(feedback);
    }
}
