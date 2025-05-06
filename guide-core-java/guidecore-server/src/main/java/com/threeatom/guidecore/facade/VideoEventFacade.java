package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.request.UserTaskAnswerPreviewDto;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.PortalUser;
import java.time.OffsetDateTime;
import java.util.List;

public interface VideoEventFacade {

    List<TaskDto> videoTasks(Integer videoId, PortalUser portalUser);

    List<TaskDto> createTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer videoId,
                             PortalUser portalUser);

    List<TaskDto> updateTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer taskId,
                             PortalUser portalUser);

    void deleteTask(Integer taskId, PortalUser portalUser);

    AnswerKeyDto taskAnswerKey(Integer taskId, PortalUser portalUser);

    List<TaskVersionDto> taskVersions(Integer taskId, PortalUser portalUser);

    UserTaskAnswersDto taskAnswers(Integer taskId, String userFilter, OffsetDateTime startDate, OffsetDateTime endDate,
                                   PortalUser portalUser);

    UserTaskAnswersDto taskAnswersPreview(Integer taskId, PortalUser portalUser);

    UserTaskAnswerDto createTaskAnswerPreview(UserTaskAnswerPreviewDto userTaskAnswerDto, Integer taskId,
                                              PortalUser portalUser);

    UserTaskAnswerDto createTaskAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto,
                                       Integer taskId, PortalUser portalUser);
}
