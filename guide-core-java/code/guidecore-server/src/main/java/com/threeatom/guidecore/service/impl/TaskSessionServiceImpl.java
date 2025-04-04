package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.TaskSession;
import com.threeatom.guidecore.mapper.TaskSessionMapper;
import com.threeatom.guidecore.service.TaskSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskSessionServiceImpl extends ServiceImpl<TaskSessionMapper, TaskSession> implements TaskSessionService {

    @Override
    @Transactional
    public void createUpdateTaskSession(TaskSessionDto taskSessionDto, Integer taskId, PortalUser portalUser) {
        TaskSession byId = getById(taskSessionDto.getSessionId());
        if (byId == null) {
            createTaskSession(taskSessionDto, taskId);
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

    private void createTaskSession(TaskSessionDto taskSessionDto, Integer taskId) {
        TaskSession taskSession = new TaskSession();
        taskSession.setId(taskSessionDto.getSessionId());
        taskSession.setTaskId(taskId);
        taskSession.setDuration(taskSessionDto.getDuration());
        taskSession.setStartTime(taskSessionDto.getStartTime());
        save(taskSession);
    }

}
