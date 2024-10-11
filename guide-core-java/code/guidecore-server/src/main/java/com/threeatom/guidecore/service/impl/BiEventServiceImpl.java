package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.request.BiEventDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEventAction;
import com.threeatom.guidecore.service.BiEventService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiEventServiceImpl implements BiEventService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishEvent(BiEventAction action, GcUser user, String visitorId) {
        eventPublisher.publishEvent(biEvent(action, user, visitorId));
    }

    private BiEventDto biEvent(BiEventAction action, GcUser user, String visitorId,
                               Map<String, String> additionalData) {
        return BiEventDto.builder()
            .powtoonUserId(user.getPowtoonUserId())
            .visitorId(visitorId)
            .action(action)
            .additionalData(additionalData)
            .build();
    }

    private BiEventDto biEvent(BiEventAction action, GcUser user, String visitorId) {
        return biEvent(action, user, visitorId, Map.of());
    }
}
