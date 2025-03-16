package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.TaskDto;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;

public interface VideoEventFacade {
    List<TaskDto> videoTasks(Integer videoId, PortalUser portalUser);
}
