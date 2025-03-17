package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskAudit;
import java.time.OffsetDateTime;

public interface TaskAuditService extends IService<TaskAudit> {
    void create(Task task, Integer userId, OffsetDateTime createdTime);
}
