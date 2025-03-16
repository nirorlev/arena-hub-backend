package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.TaskAudit;
import com.threeatom.guidecore.mapper.TaskAuditMapper;
import com.threeatom.guidecore.service.TaskAuditService;
import org.springframework.stereotype.Service;

@Service
public class TaskAuditServiceImpl extends ServiceImpl<TaskAuditMapper, TaskAudit>
    implements TaskAuditService {
}
