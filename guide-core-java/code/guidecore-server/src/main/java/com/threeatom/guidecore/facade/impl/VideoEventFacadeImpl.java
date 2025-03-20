package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.VideoEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoEventFacadeImpl implements VideoEventFacade {
    private final VideoEventService videoEventService;
    private final TaskService taskService;
    private final GcVideoService videoService;
    private final AuthorizationService authorizationService;

    @Override
    public List<TaskDto> videoTasks(Integer videoId, PortalUser portalUser) {
        verifyOriginCoursePermission(portalUser, videoId, PermitAction.VIEW);

        return taskService.videoTasks(getTaskVideoEvents(videoId));
    }

    @Override
    @Transactional
    public List<TaskDto> createTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer videoId,
                                    PortalUser portalUser) {
        GcVideo video = videoService.findByVideoId(videoId);
        verifyOriginCoursePermission(portalUser, video.getOriginCourse(), PermitAction.EDIT);

        VideoEvent taskVideoEvent = videoEventService.createTaskVideoEvent(taskDto, video, portalUser);
        taskService.createTask(taskDto, taskVideoEvent, portalUser);
        return taskService.videoTasks(getTaskVideoEvents(videoId));
    }

    @Override
    @Transactional
    public List<TaskDto> updateTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer taskId,
                                    PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        GcVideo video = videoService.findByVideoId(task.getVideoEvent().getVideoId());
        verifyOriginCoursePermission(portalUser, video.getOriginCourse(), PermitAction.EDIT);

        videoEventService.updateTaskVideoEvent(task.getVideoEvent(), taskDto, video, portalUser);
        taskService.updateTask(task, taskDto, portalUser.getUserId());
        return taskService.videoTasks(getTaskVideoEvents(task.getVideoEvent().getVideoId()));
    }

    @Override
    @Transactional
    public void deleteTask(Integer taskId, PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        verifyOriginCoursePermission(portalUser, task.getVideoEvent().getVideoId(), PermitAction.EDIT);

        taskService.deleteTask(task, portalUser.getUserId());
        videoEventService.removeById(task.getVideoEvent().getId());
    }

    @Override
    public AnswerKeyDto taskAnswerKey(Integer taskId, PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        verifyOriginCoursePermission(portalUser, task.getVideoEvent().getVideoId(), PermitAction.EDIT);

        return taskService.answerKey(task);
    }

    @Override
    public List<TaskVersionDto> taskVersions(Integer taskId, PortalUser portalUser) {
        Task currentVersion = taskService.getTask(taskId);
        verifyOriginCoursePermission(portalUser, currentVersion.getVideoEvent().getVideoId(), PermitAction.EDIT);

        return taskService.taskVersions(currentVersion);
    }

    private List<VideoEvent> getTaskVideoEvents(Integer videoId) {
        return videoEventService.videoEventsByType(videoId, VideoEventType.TASK);
    }

    private void verifyOriginCoursePermission(PortalUser portalUser, Integer videoId, PermitAction permitAction) {
        GcVideo video = videoService.findByVideoId(videoId);
        verifyOriginCoursePermission(portalUser, video.getOriginCourse(), permitAction);
    }

    private void verifyOriginCoursePermission(PortalUser portalUser, GcSubject course, PermitAction permitAction) {
        if (!authorizationService.checkAccess(course, permitAction, portalUser)) {
            throw new ForbiddenException(
                "User does not have permission to %s this course".formatted(permitAction.name()));
        }
    }
}
