package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.TaskSession;

public interface TaskSessionService extends IService<TaskSession> {
    void createUpdateTaskSession(TaskSessionDto taskSessionDto, Integer taskId, PortalUser portalUser);
}
