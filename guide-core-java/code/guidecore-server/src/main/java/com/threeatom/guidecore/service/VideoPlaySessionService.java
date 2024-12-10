package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.request.VideoViewerDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewerDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoPlaySessionService extends IService<VideoPlaySession> {
    void saveVideoPlaySession(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId);

    Optional<VideoPlaySession> getVideoPlaySession(UUID sessionId);

    List<DbAnalyticsResultDto> getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getVideoViewCountByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultDto> getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getViewersCountByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<VideoViewerDto> getVideoViewersAnalytics(VideoViewerDetailsDto filter, Integer masterId);

    Integer getVideoViewsCount(Integer videoId, Integer masterId);
}
