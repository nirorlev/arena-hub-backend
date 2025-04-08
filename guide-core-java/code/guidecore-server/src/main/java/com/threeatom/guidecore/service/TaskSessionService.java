package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.dto.response.TaskSessionsDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.TaskSession;
import java.util.Map;

public interface TaskSessionService extends IService<TaskSession> {
    void createUpdateTaskSession(TaskSessionDto taskSessionDto, Integer taskId, PortalUser portalUser);

    Map<String, TaskSessionsDto> userIdToTaskSessions(Integer taskId, PortalUser portalUser);
}
