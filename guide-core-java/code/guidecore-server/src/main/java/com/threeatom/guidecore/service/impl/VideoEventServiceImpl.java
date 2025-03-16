package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.request.TaskDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.mapper.VideoEventMapper;
import com.threeatom.guidecore.service.VideoEventService;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoEventServiceImpl extends ServiceImpl<VideoEventMapper, VideoEvent> implements VideoEventService {

    @Override
    public List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType, PortalUser portalUser) {
        return baseMapper.videoEventsByType(videoId, eventType, portalUser.getUserId(), portalUser.getMasterId());
    }

    @Override
    @Transactional
    public VideoEvent createTaskVideoEvent(TaskDto taskDto, GcVideo video, PortalUser portalUser) {
        verifyTaskVideoTime(taskDto, video);

        List<VideoEvent> videoEvents =
            baseMapper.videoEventsByType(video.getId(), VideoEventType.TASK, portalUser.getUserId(),
                portalUser.getMasterId());

        VideoEvent videoEvent = createVideoEvent(portalUser.getUserId(), taskDto.getTimestamp(), video.getId());
        saveOrUpdateBatch(optimizeNewVideoEventsOrder(videoEvent, videoEvents, taskDto.getOrder()));

        return videoEvent;
    }

    private List<VideoEvent> optimizeNewVideoEventsOrder(VideoEvent videoEvent, List<VideoEvent> videoEvents,
                                                         Integer order) {
        List<VideoEvent> videoEventsWithTheSameTimestamp =
            filterVideoEvents(videoEvents, videoEventWithSameTimestamp(videoEvent.getVideoTime()));
        return addVideoEventWithOptimizedOrder(videoEvent, videoEventsWithTheSameTimestamp, order);
    }

    private List<VideoEvent> addVideoEventWithOptimizedOrder(VideoEvent videoEvent,
                                                             List<VideoEvent> videoEventsWithTheSameTimestamp,
                                                             Integer order) {
        int videoEventsWithSameTimestampSize = videoEventsWithTheSameTimestamp.size();
        int requestedOrder = order != null ? order : videoEventsWithSameTimestampSize;
        int newOrder = Math.min(requestedOrder, videoEventsWithSameTimestampSize);

        videoEvent.setOrder(newOrder);

        for (int i = newOrder; i < videoEventsWithSameTimestampSize; i++) {
            videoEventsWithTheSameTimestamp.get(i).setOrder(i + 1);
        }

        videoEventsWithTheSameTimestamp.add(videoEvent);
        return videoEventsWithTheSameTimestamp;
    }

    private List<VideoEvent> filterVideoEvents(List<VideoEvent> videoEvents, Predicate<VideoEvent> predicate) {
        return videoEvents.stream()
            .filter(predicate)
            .sorted(Comparator.comparing(VideoEvent::getOrder))
            .collect(Collectors.toList());
    }

    private Predicate<VideoEvent> videoEventWithSameTimestamp(Integer timestamp) {
        return videoEvent -> videoEvent.getVideoTime().equals(timestamp);
    }

    private void verifyTaskVideoTime(TaskDto taskDto, GcVideo video) {
        if (taskDto.getTimestamp() > video.getVideoTime()) {
            log.error("Failed to create task for video {}. Task timestamp {} cannot be greater than video time {}"
                , video.getId(), taskDto.getTimestamp(), video.getVideoTime());
            throw new ValidationException("Task timestamp cannot be greater than video time");
        }
    }

    private VideoEvent createVideoEvent(Integer userId, Integer timestamp, Integer videoId) {
        VideoEvent videoEvent = new VideoEvent();
        videoEvent.setType(VideoEventType.TASK);
        videoEvent.setVideoTime(timestamp);
        videoEvent.setVideoId(videoId);
        videoEvent.setOwnerId(userId);
        return videoEvent;
    }
}
