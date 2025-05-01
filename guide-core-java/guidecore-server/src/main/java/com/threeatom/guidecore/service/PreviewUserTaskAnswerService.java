package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.enums.TaskType;

public interface PreviewUserTaskAnswerService {

    UserTaskAnswerDto getAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto, Task task,
                                PortalUser portalUser);

    UserTaskAnswersDto getTaskAnswers(TaskType taskType);
}
