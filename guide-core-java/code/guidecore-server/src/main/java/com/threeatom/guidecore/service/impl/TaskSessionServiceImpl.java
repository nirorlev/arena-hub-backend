package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.TaskSession;
import com.threeatom.guidecore.mapper.TaskSessionMapper;
import com.threeatom.guidecore.mapping.TaskMapping;
import com.threeatom.guidecore.service.TaskSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskSessionServiceImpl extends ServiceImpl<TaskSessionMapper, TaskSession> implements TaskSessionService {

    private final TaskMapping taskMapping;

    @Override
    @Transactional
    public void createUpdateTaskSession(TaskSessionDto taskSessionDto, Integer taskId, PortalUser portalUser) {
        TaskSession byId = getById(taskSessionDto.getSessionId());
        if (byId == null) {
            TaskSession taskSession = taskMapping.map(taskSessionDto, taskId, portalUser.getUserId());
            save(taskSession);
            return;
        }

        updateTaskSession(byId, taskSessionDto);
    }

    private void updateTaskSession(TaskSession taskSession, TaskSessionDto taskSessionDto) {
        if (taskSession.getDuration() >= taskSessionDto.getDuration()
            || !taskSession.getStartTime().equals(taskSessionDto.getStartTime())) {
            throw new ValidationException("Task session duration or start time is invalid");
        }

        taskSession.setDuration(taskSessionDto.getDuration());
        updateById(taskSession);
    }
}
