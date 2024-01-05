package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcUserAnswer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 事件的用户问题回答 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
public interface GcUserAnswerService extends IService<GcUserAnswer> {

    List<GcUserAnswer> getAnswerListByEventId(Integer eventId, Integer userId,Integer masterId);

    boolean saveUserAnswer(GcUserAnswer userAnswer);

    GcUserAnswer getMyEventAnswerByEventId(Integer eventId, Integer userId,Integer masterId);

    List<GcUserAnswer> getAllAnswerList(List<Integer> eventIds);

    List<GcUserAnswer> getAllAnswerListByEventIds(List<Integer> eventIds,Integer studentId);

    List<GcUserAnswer> getAnswerListByEvent(List<Integer> eventIds,Integer masterId,Integer userId);

    List<GcUserAnswer> getMyAnswerListByEvent(List<Integer> eventIds,Integer masterId,Integer userId);
}
