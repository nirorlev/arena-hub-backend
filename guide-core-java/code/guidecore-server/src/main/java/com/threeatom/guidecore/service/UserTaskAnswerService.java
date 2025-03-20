
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.enums.TaskType;

public interface UserTaskAnswerService extends IService<UserTaskAnswer> {

    UserTaskAnswersDto findUserTaskAnswersByTaskIdAndUserId(Integer taskId, TaskType taskType, PortalUser portalUser);

    UserTaskAnswersDto findUserTaskAnswersByTaskId(Integer taskId, TaskType taskType);

    UserTaskAnswerDto findUserTaskAnswerById(Integer userTaskAnswerId, TaskType taskType);

    UserTaskAnswer createAnswer(com.threeatom.guidecore.dto.request.UserTaskAnswerDto userTaskAnswerDto, Task task,
                                PortalUser portalUser);
}
