package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEventAction;
import com.threeatom.guidecore.event.BiEvent;
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
    public void publishEvent(BiEventAction event) {
        Optional<HttpServletRequest> servletRequestOptional = RequestUtil.extractCurrentRequest();

        if (servletRequestOptional.isEmpty()) {
            eventPublisher.publishEvent(biEvent(event, null, null));
            return;
        }

        HttpServletRequest servletRequest = servletRequestOptional.get();
        GcUser currentUser = userService.getCurrentUser(servletRequest);
        String visitorId = RequestUtil.getCookieValue(servletRequest, "visitorid").orElse(null);

        eventPublisher.publishEvent(biEvent(event, currentUser, visitorId));
    }

    private BiEvent biEvent(BiEventAction event, GcUser user, String visitorId, Map<String, String> additionalData) {
        BiEvent biEvent = new BiEvent(this, event);
        biEvent.setPowtoonUserId(user.getPowtoonUserId());
        biEvent.setVisitorId(visitorId);
        biEvent.setAdditionalData(additionalData);
        return biEvent;
    }

    private BiEvent biEvent(BiEventAction event, GcUser user, String visitorId) {
        return biEvent(event, user, visitorId, Map.of());
    }
}
