
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import java.util.List;

public interface TaskService extends IService<Task> {
    List<TaskDto> videoTasks(List<VideoEvent> taskVideoEvents);
}
