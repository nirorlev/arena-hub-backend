package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.request.BiEventDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEvent;
import com.threeatom.guidecore.service.BiEventService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.RequestUtil;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiEventServiceImpl implements BiEventService {

    private final ApplicationEventPublisher eventPublisher;
    private final GcUserService userService;

    @Override
    public void publishEvent(BiEvent event) {
        Optional<HttpServletRequest> servletRequest = RequestUtil.extractCurrentRequest();
        servletRequest.ifPresent(request -> {
            GcUser currentUser = userService.getCurrentUser(request);
            String visitorId = RequestUtil.getCookieValue(request, "visitorid").orElse(null);

            eventPublisher.publishEvent(biEvent(event, currentUser, visitorId));
        });
    }

    private BiEventDto biEvent(BiEvent event, GcUser user, String visitorId,
                               Map<String, String> additionalData) {
        return BiEventDto.builder()
            .powtoonUserId(user.getPowtoonUserId())
            .visitorId(visitorId)
            .action(event)
            .additionalData(additionalData)
            .build();
    }

    private BiEventDto biEvent(BiEvent event, GcUser user, String visitorId) {
        return biEvent(event, user, visitorId, Map.of());
    }
}
