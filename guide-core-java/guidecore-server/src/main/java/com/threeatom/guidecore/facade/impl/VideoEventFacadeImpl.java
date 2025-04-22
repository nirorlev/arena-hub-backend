package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoEventService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoEventFacadeImpl implements VideoEventFacade {
    private final VideoEventService videoEventService;
    private final TaskService taskService;
    private final GcVideoService videoService;
    private final AuthorizationService authorizationService;
    private final UserTaskAnswerService userTaskAnswerService;
    private final CourseEnrollmentService courseEnrollmentService;

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
        GcSubject originCourse = video.getOriginCourse();
        verifyOriginCoursePermission(portalUser, originCourse, PermitAction.EDIT);

        VideoEvent taskVideoEvent = videoEventService.createTaskVideoEvent(taskDto, video, portalUser);
        taskService.createTask(taskDto, taskVideoEvent, originCourse.getId(), portalUser);
        return taskService.videoTasks(getTaskVideoEvents(videoId));
    }

    @Override
    @Transactional
    public List<TaskDto> updateTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer taskId,
                                    PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        GcVideo video = videoService.findByVideoId(task.getVideoEvent().getVideoId());
        verifyOriginCoursePermission(portalUser, video.getOriginCourse(), PermitAction.EDIT);

        videoEventService.updateTaskVideoEvent(task.getVideoEvent(), taskDto, video);
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

    @Override
    public UserTaskAnswersDto taskAnswers(Integer taskId, String userFilter, OffsetDateTime startDate,
                                          OffsetDateTime endDate, PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        Integer videoId = task.getVideoEvent().getVideoId();
        CourseEnrollment activeEnrollment =
            courseEnrollmentService.getActiveEnrollment(task.getCourseId(), portalUser.getUserId());
        OffsetDateTime answersStartDate = startDate == null ? activeEnrollment.getStartDate() : startDate;

        if ("all".equals(userFilter)) {
            verifyOriginCoursePermission(portalUser, videoId, PermitAction.EDIT);
            return userTaskAnswerService.findUserTaskAnswersByTaskId(taskId, task.getType(), answersStartDate, endDate);
        }

        if ("me".equals(userFilter)) {
            verifyOriginCoursePermission(portalUser, videoId, PermitAction.VIEW);
            return userTaskAnswerService.findUserTaskAnswersByTaskIdAndUserId(taskId, task.getType(), answersStartDate, endDate, portalUser);
        }

        log.error("Invalid user filter passed: {} for task {}", userFilter, taskId);
        throw new ValidationException("Invalid user filter passed: %s".formatted(userFilter));
    }

    @Override
    public UserTaskAnswerDto createTaskAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto,
                                              Integer taskId, PortalUser portalUser) {
        Task task = taskService.getTask(taskId);
        verifyOriginCoursePermission(portalUser, task.getVideoEvent().getVideoId(), PermitAction.VIEW);

        CourseEnrollment activeEnrollment =
            courseEnrollmentService.getActiveEnrollment(task.getCourseId(), portalUser.getUserId());
        UserTaskAnswer userTaskAnswer =
            userTaskAnswerService.createAnswer(userTaskAnswerDto, task, activeEnrollment, portalUser);

        return userTaskAnswerService.findUserTaskAnswerById(userTaskAnswer.getId(), task.getType());
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
