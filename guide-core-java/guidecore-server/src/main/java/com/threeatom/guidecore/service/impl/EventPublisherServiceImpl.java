package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEventAction;
import com.threeatom.guidecore.event.BiEvent;
import com.threeatom.guidecore.event.entity.ChannelUpdatedEvent;
import com.threeatom.guidecore.event.entity.ContentGroupUpdatedEvent;
import com.threeatom.guidecore.event.entity.CourseUpdatedEvent;
import com.threeatom.guidecore.event.entity.UserUpdatedEvent;
import com.threeatom.guidecore.event.entity.VideoItemUpdatedEvent;
import com.threeatom.guidecore.service.EventPublisherService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.util.RequestUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherServiceImpl implements EventPublisherService {

    private final ApplicationEventPublisher eventPublisher;
    private final GcUserService userService;

    @Override
    public void publishBiEvent(BiEventAction eventAction, GcUser user) {
        Optional<HttpServletRequest> servletRequestOptional = RequestUtil.extractCurrentRequest();

        if (servletRequestOptional.isEmpty()) {
            eventPublisher.publishEvent(biEvent(eventAction, user, null));
            return;
        }

        eventPublisher.publishEvent(biEvent(eventAction, user, getVisitorId(servletRequestOptional.get())));
    }

    @Override
    public void publishBiEvent(BiEventAction eventAction) {
        Optional<HttpServletRequest> servletRequestOptional = RequestUtil.extractCurrentRequest();

        if (servletRequestOptional.isEmpty()) {
            eventPublisher.publishEvent(biEvent(eventAction, null, null));
            return;
        }

        BiEvent event = biEvent(eventAction, userService.getCurrentUser(
            servletRequestOptional.get()), getVisitorId(servletRequestOptional.get()));
        eventPublisher.publishEvent(event);
    }

    private BiEvent biEvent(BiEventAction event, GcUser user, String visitorId, Map<String, String> additionalData) {
        BiEvent biEvent = new BiEvent(this, event);
        biEvent.setPowtoonUserId(user == null ? null : user.getPowtoonUserId());
        biEvent.setVisitorId(visitorId);
        biEvent.setAdditionalData(additionalData);
        return biEvent;
    }

    private BiEvent biEvent(BiEventAction event, GcUser user, String visitorId) {
        return biEvent(event, user, visitorId, Map.of());
    }

    private String getVisitorId(HttpServletRequest request) {
        return RequestUtil.getCookieValue(request, "visitorid").orElse(null);
    }

    @Override
    public void publishUserUpdated(Integer userId) {
        eventPublisher.publishEvent(new UserUpdatedEvent(this, userId));
    }

    @Override
    public void publishUserUpdated(List<Integer> userIds) {
        userIds.forEach(this::publishUserUpdated);
    }

    @Override
    public void publishContentGroupUpdated(Integer contentGroupId) {
        eventPublisher.publishEvent(new ContentGroupUpdatedEvent(this, contentGroupId));
    }

    @Override
    public void publishVideoUpdated(Integer videoId) {
        eventPublisher.publishEvent(new VideoItemUpdatedEvent(this, videoId));
    }

    @Override
    public void publishCourseUpdated(Integer courseId) {
        eventPublisher.publishEvent(new CourseUpdatedEvent(this, courseId));
    }

    @Override
    public void publishCourseUpdated(List<Integer> courseIds) {
        courseIds.forEach(this::publishCourseUpdated);
    }

    @Override
    public void publishChannelUpdated(Integer channelId) {
        eventPublisher.publishEvent(new ChannelUpdatedEvent(this, channelId));
    }

    @Override
    public void publishChannelUpdated(List<Integer> channelIds) {
        channelIds.forEach(this::publishChannelUpdated);
    }
}
