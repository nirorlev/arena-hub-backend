package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.FeedbackAverageDto;
import com.threeatom.guidecore.dto.response.FeedbacksDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.facade.FeedbackFacade;
import com.threeatom.guidecore.service.FeedbackService;
import com.threeatom.guidecore.service.CourseService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.TaskService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackFacadeImpl implements FeedbackFacade {
    private final AuthorizationService authorizationService;
    private final FeedbackService feedbackService;
    private final CourseService courseService;
    private final GcVideoService videoService;
    private final TaskService taskService;

    @Override
    public FeedbackAverageDto createOrUpdateFeedback(FeedbackItemType itemType, Integer itemId,
                                              com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                              PortalUser portalUser) {
        validatePermission(itemType, itemId, portalUser, PermitAction.RATE);

        return feedbackService.createOrUpdateFeedback(itemType, itemId, feedbackDto, portalUser);
    }

    @Override
    public FeedbackAverageDto updateFeedback(Long feedbackId,
                                             com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                             PortalUser portalUser) {
        Feedback feedback = feedbackService.getById(feedbackId);
        validateOwnership(portalUser, feedback);

        validatePermission(feedback.getItemType(), feedback.getItemId(), portalUser, PermitAction.RATE);

        return feedbackService.updateFeedback(feedback, feedbackDto);
    }

    @Override
    public FeedbackAverageDto deleteFeedback(Long feedbackId, PortalUser portalUser) {
        Feedback feedback = feedbackService.getById(feedbackId);

        validateOwnership(portalUser, feedback);
        validatePermission(feedback.getItemType(), feedback.getItemId(), portalUser, PermitAction.RATE);

        return feedbackService.deleteFeedback(feedback);
    }

    @Override
    public FeedbacksDto userFeedbacks(OffsetDateTime startDate, OffsetDateTime endDate, PortalUser portalUser) {
        return feedbackService.userFeedbacks(getStartDate(startDate), getEndDate(endDate), portalUser);
    }

    @Override
    public FeedbacksDto feedbacks(FeedbackItemType itemType, Integer itemId, OffsetDateTime startDate,
                                  OffsetDateTime endDate, String users, PortalUser portalUser) {

        if ("me".equals(users)) {
            return feedbackService.feedbacks(itemType, itemId, getStartDate(startDate), getEndDate(endDate),
                portalUser);
        }
        if ("all".equals(users)) {
            validatePermission(itemType, itemId, portalUser, PermitAction.EDIT);
            return feedbackService.feedbacks(itemType, itemId, getStartDate(startDate), getEndDate(endDate));
        }

        log.error("Invalid users parameter: {}", users);
        throw new ValidationException("Invalid users parameter");
    }

    @Override
    public FeedbackAverageDto patchFeedback(Long feedbackId, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                     PortalUser portalUser) {
        Feedback feedback = feedbackService.getById(feedbackId);
        validateOwnership(portalUser, feedback);

        validatePermission(feedback.getItemType(), feedback.getItemId(), portalUser, PermitAction.RATE);
        return feedbackService.patchFeedback(feedback, feedbackDto);
    }

    private void validatePermission(FeedbackItemType itemType, Integer itemId, PortalUser portalUser,
                                    PermitAction permitAction) {
        switch (itemType) {
            case VIDEO -> validateVideoFeedback(itemId, portalUser, permitAction);
            case COURSE -> validateCourseFeedback(itemId, portalUser, permitAction);
            case TASK -> validateTaskFeedback(itemId, portalUser, permitAction);
        }
    }

    private void validateTaskFeedback(Integer itemId, PortalUser portalUser, PermitAction permitAction) {
        Task task = taskService.getTask(itemId);
        GcVideo taskVideo = videoService.findByVideoId(task.getVideoEvent().getVideoId());

        if (!authorizationService.checkAccess(taskVideo.getOriginCourse(), permitAction, portalUser)) {
            throw new ForbiddenException("User don't have permission to %s this task".formatted(permitAction.name()));
        }
    }

    private void validateCourseFeedback(Integer courseId, PortalUser portalUser, PermitAction permitAction) {
        Course course = courseService.getById(courseId);
        if (!authorizationService.checkAccess(course, permitAction, portalUser)) {
            throw new ForbiddenException("User don't have permission to %s this course".formatted(permitAction.name()));
        }
    }

    private void validateVideoFeedback(Integer videoId, PortalUser portalUser, PermitAction permitAction) {
        GcVideo video = videoService.findByVideoId(videoId);
        if (!authorizationService.checkAccess(video, permitAction, portalUser)) {
            throw new ForbiddenException("User don't have permission to %s this video".formatted(permitAction.name()));
        }
    }

    private void validateOwnership(PortalUser portalUser, Feedback feedback) {
        if (!feedback.getUserId().equals(portalUser.getUserId())) {
            throw new ForbiddenException("User don't have permission to update this feedback");
        }
    }

    private OffsetDateTime getStartDate(OffsetDateTime startDate) {
        if (startDate == null) {
            return OffsetDateTime.MIN;
        }

        return startDate;
    }

    private OffsetDateTime getEndDate(OffsetDateTime endDate) {
        if (endDate == null) {
            return OffsetDateTime.now();
        }

        return endDate;
    }
}
