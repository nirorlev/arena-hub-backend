package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.mapper.TaskMapper;
import com.threeatom.guidecore.mapping.TaskMapping;
import com.threeatom.guidecore.service.TaskService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final TaskMapping taskMapping;

    @Override
    public List<TaskDto> videoTasks(List<VideoEvent> taskVideoEvents) {
        return taskMapping.map(taskVideoEvents);
    }
}
