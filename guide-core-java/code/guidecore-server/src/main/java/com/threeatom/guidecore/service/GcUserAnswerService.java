package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAnswer;
import java.util.List;

public interface GcUserAnswerService extends IService<GcUserAnswer> {

    List<GcUserAnswer> getAnswerListByEventId(Integer eventId, Integer userId, Integer masterId);

    boolean saveUserAnswer(GcUserAnswer userAnswer);

    GcUserAnswer getMyEventAnswerByEventId(Integer eventId, Integer userId, Integer masterId);

    List<GcUserAnswer> getAllAnswerList(List<Integer> eventIds);

    List<GcUserAnswer> getAllAnswerListByEventIds(List<Integer> eventIds, Integer studentId);

    List<GcUserAnswer> getAnswerListByEvent(List<Integer> eventIds, Integer masterId, Integer userId);

    List<GcUserAnswer> getMyAnswerListByEvent(
            List<Integer> eventIds, Integer masterId, Integer userId);
}
