package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserFabulous;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author huangpei
 * @title: GcUserFabulousMapper
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/29/02914:13
 */
public interface GcUserFabulousMapper extends BaseMapper<GcUserFabulous> {

    GcUserFabulous getUserFabulous(@Param("entity") GcUserFabulous gcUserFabulous);

    Integer getEventFabulousNum(@Param("eventId")Integer eventId,@Param("targetUserId")Integer targetUserId,@Param("commentId")Integer commentId);

    Integer getVideoFabulousNum(@Param("videoId")Integer videoId,@Param("targetUserId")Integer targetUserId,@Param("commentId")Integer commentId);

    List<GcUserFabulous> getEventFabulousNumList(Integer eventId, List<Integer> targetUserIdList);
}
