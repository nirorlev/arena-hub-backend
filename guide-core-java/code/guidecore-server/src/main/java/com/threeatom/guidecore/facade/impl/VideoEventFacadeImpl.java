package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.VideoEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoEventFacadeImpl implements VideoEventFacade {
    private final VideoEventService videoEventService;
    private final TaskService taskService;
    private final GcVideoService videoService;
    private final AuthorizationService authorizationService;

    @Override
    public List<TaskDto> videoTasks(Integer videoId, PortalUser portalUser) {
        verifyPermission(portalUser, videoId, PermitAction.VIEW);

        return taskService.videoTasks(getTaskVideoEvents(videoId, portalUser));
    }

    private List<VideoEvent> getTaskVideoEvents(Integer videoId, PortalUser portalUser) {
        return videoEventService.videoEventsByType(videoId, VideoEventType.TASK, portalUser);
    }

    private void verifyPermission(PortalUser portalUser, Integer videoId, PermitAction permitAction) {
        GcVideo video = videoService.findByVideoId(videoId);
        verifyPermission(portalUser, video.getOriginCourse(), permitAction);
    }

    private void verifyPermission(PortalUser portalUser, GcSubject course, PermitAction permitAction) {
        if (!authorizationService.checkAccess(course, permitAction, portalUser)) {
            throw new ForbiddenException(
                "User does not have permission to %s this course".formatted(permitAction.name()));
        }
    }
}
