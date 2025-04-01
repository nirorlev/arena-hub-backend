package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.dto.response.TaskSessionsDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.TaskSession;
import com.threeatom.guidecore.mapper.TaskSessionMapper;
import com.threeatom.guidecore.mapping.TaskMapping;
import com.threeatom.guidecore.service.TaskSessionService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskSessionServiceImpl extends ServiceImpl<TaskSessionMapper, TaskSession> implements TaskSessionService {

    private final AuthorizationService authorizationService;
    private final TaskMapping taskMapping;

    @Override
    @Transactional
    public void createUpdateTaskSession(TaskSessionDto taskSessionDto, Integer taskId, PortalUser portalUser) {
        TaskSession byId = getById(taskSessionDto.getSessionId());
        if (byId == null) {
            TaskSession taskSession = taskMapping.mapTaskSession(taskSessionDto, taskId, portalUser.getUserId());
            save(taskSession);
            return;
        }

        updateTaskSession(byId, taskSessionDto);
    }

    @Override
    public Map<String, TaskSessionsDto> taskSession(Integer taskId, PortalUser portalUser) {
        if (!authorizationService.checkAccess(PortalAction.ACCESS_ANALYTICS, portalUser)) {
            throw new ForbiddenException("No permission to access task sessions");
        }

        return getTaskSessions(taskId).stream()
            .collect(Collectors.groupingBy(TaskSession::getUserId)).entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()),
                entry -> createTaskSessionsDto(entry.getValue())));
    }

    private void updateTaskSession(TaskSession taskSession, TaskSessionDto taskSessionDto) {
        if (taskSession.getDuration() >= taskSessionDto.getDuration()
            || !taskSession.getStartTime().equals(taskSessionDto.getStartTime())) {
            throw new ValidationException("Task session duration or start time is invalid");
        }

        taskSession.setDuration(taskSessionDto.getDuration());
        updateById(taskSession);
    }

    private TaskSessionsDto createTaskSessionsDto(List<TaskSession> taskSessions) {
        TaskSessionsDto taskSessionsDto = new TaskSessionsDto();
        taskSessionsDto.setSessions(taskMapping.mapTaskSessions(taskSessions));
        return taskSessionsDto;
    }

    private List<TaskSession> getTaskSessions(Integer taskId) {
        QueryWrapper<TaskSession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        return list(queryWrapper);
    }
}
