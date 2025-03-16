
package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import java.util.List;

public interface VideoEventService extends IService<VideoEvent> {
    List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType, PortalUser portalUser);
}
