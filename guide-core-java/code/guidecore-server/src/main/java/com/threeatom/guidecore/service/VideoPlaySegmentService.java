package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultViewPerSecondDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.request.VideoViewPerSecondDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import java.time.OffsetDateTime;
import java.util.List;

public interface VideoPlaySegmentService extends IService<VideoPlaySegment> {
    void saveVideoPlaySegment(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId);

    List<DbAnalyticsResultDto> getVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getVideoWatchingTimeByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultDto> getAverageVideoWatchingTimeAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultDto> getDropOffRateAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultDto> getEngagementRateAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getEngagementRateByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getDropOffRateByVideoAnalytics(AnalyticsFilterDto filter, Integer masterId);

    List<DbAnalyticsResultViewPerSecondDto> videoViewsPerSecondAnalytics(VideoViewPerSecondDto filter,
                                                                         Integer masterId);
}
