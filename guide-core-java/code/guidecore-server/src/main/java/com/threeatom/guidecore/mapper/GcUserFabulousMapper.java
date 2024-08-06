package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserFabulous;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcUserFabulousMapper extends BaseMapper<GcUserFabulous> {

    GcUserFabulous getUserFabulous(@Param("entity") GcUserFabulous gcUserFabulous);

    Integer getEventFabulousNum(
            @Param("eventId") Integer eventId,
            @Param("targetUserId") Integer targetUserId,
            @Param("commentId") Integer commentId);

    Integer getVideoFabulousNum(
            @Param("videoId") Integer videoId,
            @Param("targetUserId") Integer targetUserId,
            @Param("commentId") Integer commentId);

    List<GcUserFabulous> getEventFabulousNumList(Integer eventId, List<Integer> targetUserIdList);
}
