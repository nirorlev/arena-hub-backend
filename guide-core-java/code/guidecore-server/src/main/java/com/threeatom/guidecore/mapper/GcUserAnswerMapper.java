package com.threeatom.guidecore.mapper;

import com.threeatom.guidecore.entity.GcUserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 事件的用户问题回答 Mapper 接口
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Component
public interface GcUserAnswerMapper extends BaseMapper<GcUserAnswer> {

    List<GcUserAnswer> selectGetAnswerListByEventId(Integer eventId, Integer userId,Integer masterId);

    List<GcUserAnswer> getAllAnswerListByEventIds(@Param("eventIds") List<Integer> eventIds,@Param("studentId") Integer studentId);

    List<Map<String,Object>> getUserAnswerNum(List<Integer> userIds, String startDate, String endDate);

    List<GcUserAnswer> getAnswerListByEvent(@Param("eventIds") List<Integer> eventIds,@Param("masterId")Integer masterId,@Param("userId")Integer userId,@Param("type")Integer type);

    List<GcUserAnswer> getMyAnswerListByEvent(@Param("eventIds") List<Integer> eventIds,@Param("masterId")Integer masterId,@Param("userId")Integer userId);

    List<GcUserAnswer> getAnswerLearningRecords(@Param("sub0Id")Integer sub0Id,@Param("sub1Id")Integer sub1Id,@Param("videoId")Integer videoId,@Param("userId")Integer userId,@Param("masterId")Integer masterId);
}
