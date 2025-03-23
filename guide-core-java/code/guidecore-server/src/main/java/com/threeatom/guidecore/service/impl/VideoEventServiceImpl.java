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
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoEventServiceImpl extends ServiceImpl<VideoEventMapper, VideoEvent> implements VideoEventService {

    @Override
    public List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType) {
        return baseMapper.videoEventsByType(videoId, eventType);
    }

    @Override
    public List<VideoEvent> videoEventsByType(List<Integer> videoIds, VideoEventType eventType) {
        if (CollectionUtils.isEmpty(videoIds)) {
            return List.of();
        }

        return baseMapper.getVideoEventsByVideoIds(videoIds, eventType);
    }

    @Override
    @Transactional
    public VideoEvent createTaskVideoEvent(TaskDto taskDto, GcVideo video, PortalUser portalUser) {
        verifyTaskVideoTime(taskDto, video);

        List<VideoEvent> videoEvents =
            baseMapper.videoEventsByTypeAndTimestamp(video.getId(), VideoEventType.TASK, taskDto.getTimestamp());

        VideoEvent videoEvent = createVideoEvent(portalUser.getUserId(), taskDto.getTimestamp(), video.getId());
        List<VideoEvent> orderedVideoEvents =
            addVideoEvent(videoEvent, videoEvents, taskDto.getOrder());
        saveOrUpdateBatch(orderedVideoEvents);

        return videoEvent;
    }

    @Override
    @Transactional
    public void updateTaskVideoEvent(VideoEvent videoEvent, TaskDto taskDto, GcVideo video) {
        verifyTaskVideoTime(taskDto, video);

        List<VideoEvent> videoEvents =
            baseMapper.videoEventsByTypeAndTimestamp(video.getId(), VideoEventType.TASK, taskDto.getTimestamp());

        List<VideoEvent> reorderedOtherExistingVideoEvents = reorderOtherExistingVideoEvents(videoEvent, videoEvents);

        videoEvent.setVideoTime(taskDto.getTimestamp());
        List<VideoEvent> orderVideoEvents =
            addVideoEvent(videoEvent, reorderedOtherExistingVideoEvents, taskDto.getOrder());
        updateBatchById(orderVideoEvents);
    }

    private List<VideoEvent> reorderOtherExistingVideoEvents(VideoEvent videoEvent, List<VideoEvent> videoEvents) {
        List<VideoEvent> otherExistingVideoEvents = excludeVideoEvent(videoEvents, videoEvent.getId());

        for (int i = 0; i < otherExistingVideoEvents.size(); i++) {
            otherExistingVideoEvents.get(i).setOrder(i);
        }

        return otherExistingVideoEvents;
    }

    private List<VideoEvent> addVideoEvent(VideoEvent videoEvent, List<VideoEvent> videoEvents, Integer order) {
        int videoEventsSize = videoEvents.size();
        int requestedOrder = order != null ? order : videoEventsSize;
        int newOrder = Math.min(requestedOrder, videoEventsSize);

        for (int i = newOrder; i < videoEventsSize; i++) {
            videoEvents.get(i).setOrder(i + 1);
        }

        videoEvent.setOrder(newOrder);
        videoEvents.add(videoEvent);
        return videoEvents;
    }

    private List<VideoEvent> excludeVideoEvent(List<VideoEvent> videoEvents, Integer videoEventId) {
        return videoEvents.stream()
            .filter(videoEvent -> !videoEvent.getId().equals(videoEventId))
            .collect(Collectors.toList());
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
