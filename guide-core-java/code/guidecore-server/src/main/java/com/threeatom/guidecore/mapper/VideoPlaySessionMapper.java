package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.VideoPlaySession;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VideoPlaySessionMapper extends BaseMapper<VideoPlaySession> {
    List<DbAnalyticsResultDto> getVideoViewCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultDto> getTrendVideoViewCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultDto> getViewersCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultDto> getTrendViewersCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getVideoViewCountByVideoAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);

    List<DbAnalyticsResultVideoIdDto> getViewersCountByVideoAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);
}
