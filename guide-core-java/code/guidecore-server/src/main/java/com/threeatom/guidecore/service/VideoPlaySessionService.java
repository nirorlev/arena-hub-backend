package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface VideoPlaySessionService extends IService<VideoPlaySession> {
    void saveVideoPlaySession(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId);

    @Transactional(readOnly = true)
    Optional<VideoPlaySession> getVideoPlaySession(UUID sessionId);
}
