package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.VideoPlaySession;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VideoPlaySessionMapper extends BaseMapper<VideoPlaySession> {
    List<DbAnalyticsResultDto> getVideoViewCountAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("interval") String interval,
        @Param("masterId") Integer masterId);
}
