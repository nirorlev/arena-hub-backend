package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.request.BiEventDto;
import com.threeatom.guidecore.entity.GcUser;
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
    public void publishEvent(BiEventAction action, GcUser user) {
        eventPublisher.publishEvent(biEvent(action, user));
    }

    private BiEventDto biEvent(BiEventAction action, GcUser user) {
        return BiEventDto.builder()
            .userId(user.getId())
            .value(user.getUsername())
            .action(action)
            .build();
    }
}
