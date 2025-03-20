package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskAudit;
import com.threeatom.guidecore.mapper.TaskAuditMapper;
import com.threeatom.guidecore.service.TaskAuditService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskAuditServiceImpl extends ServiceImpl<TaskAuditMapper, TaskAudit> implements TaskAuditService {

    @Override
    @Transactional
    public void create(Task task, Integer userId, OffsetDateTime createdTime) {
        TaskAudit taskAudit = new TaskAudit();
        taskAudit.setTaskId(task.getId());
        taskAudit.setCreatedByUserId(userId);
        taskAudit.setPreviousTaskVersion(task.getVersion());
        taskAudit.setPreviousTaskState(task);
        taskAudit.setCreatedTime(createdTime);
        save(taskAudit);
    }

    @Override
    public List<TaskAudit> findByTaskId(Integer taskId) {
        return baseMapper.findByTaskId(taskId);
    }
}
