package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.TaskSession;
import com.threeatom.guidecore.mapper.TaskSessionMapper;
import com.threeatom.guidecore.service.TaskSessionService;
import org.springframework.stereotype.Service;

@Service
public class TaskSessionServiceImpl extends ServiceImpl<TaskSessionMapper, TaskSession> implements TaskSessionService {
}
