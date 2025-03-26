package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.facade.FeedbackFacade;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.FeedbackService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.TaskService;
import com.threeatom.guidecore.service.VideoEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackFacadeImpl implements FeedbackFacade {
    private final AuthorizationService authorizationService;
    private final FeedbackService feedbackService;
    private final GcSubjectService courseService;
    private final GcVideoService videoService;
    private final TaskService taskService;


    @Override
    public FeedbackDto createFeedback(FeedbackItemType itemType, Integer itemId,
                                      com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                      PortalUser portalUser) {
        validatePermission(itemType, itemId, portalUser);

        return feedbackService.createFeedback(itemType, itemId, feedbackDto, portalUser);
    }


    @Override
    public FeedbackDto updateFeedback(Integer feedbackId, com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                      PortalUser portalUser) {
        Feedback feedback = feedbackService.getById(feedbackId);
        if (!feedback.getUserId().equals(portalUser.getUserId())) {
            throw new ForbiddenException("User don't have permission to update this feedback");
        }

        validatePermission(feedback.getItemType(), feedback.getItemId(), portalUser);

        return feedbackService.updateFeedback(feedback, feedbackDto);
    }

    private void validatePermission(FeedbackItemType itemType, Integer itemId, PortalUser portalUser) {
        switch (itemType) {
            case VIDEO -> validateVideoFeedback(itemId, portalUser);
            case COURSE -> validateCourseFeedback(itemId, portalUser);
            case TASK -> validateTaskFeedback(itemId, portalUser);
        }
    }

    private void validateTaskFeedback(Integer itemId, PortalUser portalUser) {
        Task task = taskService.getTask(itemId);
        GcVideo taskVideo = videoService.findByVideoId(task.getVideoEvent().getVideoId());

        if (!authorizationService.checkAccess(taskVideo.getOriginCourse(), PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("User don't have permission to view this task");
        }
    }

    private void validateCourseFeedback(Integer courseId, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("User don't have permission to view this course");
        }
    }

    private void validateVideoFeedback(Integer videoId, PortalUser portalUser) {
        GcVideo video = videoService.findByVideoId(videoId);
        if (!authorizationService.checkAccess(video, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("User don't have permission to view this video");
        }
    }
}
