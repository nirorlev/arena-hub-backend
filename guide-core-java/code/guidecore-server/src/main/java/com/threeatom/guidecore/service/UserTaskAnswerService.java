package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.ProgressDetailsDto;
import com.threeatom.guidecore.dto.response.TaskProgressDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.enums.TaskType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface UserTaskAnswerService extends IService<UserTaskAnswer> {

    UserTaskAnswersDto findUserTaskAnswersByTaskIdAndUserId(Integer taskId, TaskType taskType, OffsetDateTime startDate,
                                                            OffsetDateTime endDate, PortalUser portalUser);

    UserTaskAnswersDto findUserTaskAnswersByTaskId(Integer taskId, TaskType taskType, OffsetDateTime startDate,
                                                   OffsetDateTime endDate);

    UserTaskAnswerDto findUserTaskAnswerById(Integer userTaskAnswerId, TaskType taskType);

    UserTaskAnswer createAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto, Task task,
                                PortalUser portalUser);

    Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgress(List<Integer> taskIds, PortalUser portalUser);
}
