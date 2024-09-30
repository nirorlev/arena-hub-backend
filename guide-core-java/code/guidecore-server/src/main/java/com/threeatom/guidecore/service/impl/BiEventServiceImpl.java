package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.request.BiEventDto;
import com.threeatom.guidecore.enums.BiEventAction;
import com.threeatom.guidecore.service.BiEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiEventServiceImpl implements BiEventService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishEvent(BiEventAction action, Integer userId) {
        eventPublisher.publishEvent(biEvent(action, userId));
    }

    private BiEventDto biEvent(BiEventAction action, Integer userId) {
        return BiEventDto.builder()
            .userId(userId)
            .action(action)
            .build();
    }
}
