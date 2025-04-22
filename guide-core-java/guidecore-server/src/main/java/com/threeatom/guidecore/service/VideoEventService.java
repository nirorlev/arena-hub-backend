package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.TaskDto;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import java.util.List;

public interface VideoEventService extends IService<VideoEvent> {
    List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType);

    VideoEvent createTaskVideoEvent(TaskDto taskDto, GcVideo video, PortalUser portalUser);

    List<VideoEvent> videoEventsByType(List<Integer> videoIds, VideoEventType eventType);

    void updateTaskVideoEvent(VideoEvent videoEvent, TaskDto taskDto, GcVideo video);
}
