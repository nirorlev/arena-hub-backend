package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoPlaySessionService extends IService<VideoPlaySession> {
    void saveVideoPlaySession(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId);

    Optional<VideoPlaySession> getVideoPlaySession(UUID sessionId);

    List<DbAnalyticsResultDto> getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId);
}
