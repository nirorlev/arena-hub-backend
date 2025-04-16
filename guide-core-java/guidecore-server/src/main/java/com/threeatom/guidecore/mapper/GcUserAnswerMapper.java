package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserAnswer;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface GcUserAnswerMapper extends BaseMapper<GcUserAnswer> {

    List<GcUserAnswer> selectGetAnswerListByEventId(
            Integer eventId, Integer userId, Integer masterId);

    List<GcUserAnswer> getAllAnswerListByEventIds(
            @Param("eventIds") List<Integer> eventIds, @Param("studentId") Integer studentId);

    List<Map<String, Object>> getUserAnswerNum(
            List<Integer> userIds, String startDate, String endDate);

    List<GcUserAnswer> getAnswerListByEvent(
            @Param("eventIds") List<Integer> eventIds,
            @Param("masterId") Integer masterId,
            @Param("userId") Integer userId,
            @Param("type") Integer type);

    List<GcUserAnswer> getMyAnswerListByEvent(
            @Param("eventIds") List<Integer> eventIds,
            @Param("masterId") Integer masterId,
            @Param("userId") Integer userId);

    List<GcUserAnswer> getAnswerLearningRecords(
            @Param("sub0Id") Integer sub0Id,
            @Param("sub1Id") Integer sub1Id,
            @Param("videoId") Integer videoId,
            @Param("userId") Integer userId,
            @Param("masterId") Integer masterId);
}
