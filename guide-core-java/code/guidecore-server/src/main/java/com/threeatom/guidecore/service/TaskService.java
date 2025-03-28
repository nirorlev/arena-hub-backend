
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import java.util.List;

public interface TaskService extends IService<Task> {
    List<TaskDto> videoTasks(List<VideoEvent> taskVideoEvents);

    void createTask(com.threeatom.guidecore.dto.request.TaskDto taskDto, VideoEvent videoEvent, PortalUser portalUser);

    Task getTask(Integer taskId);

    void updateTask(Task task, com.threeatom.guidecore.dto.request.TaskDto taskDto, Integer userId);

    void deleteTask(Task task, Integer userId);

    AnswerKeyDto answerKey(Task task);

    List<TaskVersionDto> taskVersions(Task currentVersion);
}
