package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VideoPlaySegmentMapper extends BaseMapper<VideoPlaySegment> {

    Integer saveOrUpdateSegment(VideoPlaySegment videoPlaySegment);

    List<DbAnalyticsResultDto> getVideoWatchingTimeAnalytics(
        @Param("filter") AnalyticsFilterDto filter,
        @Param("masterId") Integer masterId);
}
