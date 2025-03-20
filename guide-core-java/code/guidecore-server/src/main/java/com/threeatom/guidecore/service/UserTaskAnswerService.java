
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.enums.TaskType;

public interface UserTaskAnswerService extends IService<UserTaskAnswer> {

    UserTaskAnswersDto findByTaskIdAndUserId(Integer taskId, TaskType taskType, PortalUser portalUser);

    UserTaskAnswersDto findByTaskId(Integer taskId, TaskType taskType);
}
