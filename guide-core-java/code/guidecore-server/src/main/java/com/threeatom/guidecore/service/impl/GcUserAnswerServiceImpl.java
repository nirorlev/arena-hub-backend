package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcUserAnswer;
import com.threeatom.guidecore.mapper.GcUserAnswerMapper;
import com.threeatom.guidecore.service.GcUserAnswerService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GcUserAnswerServiceImpl extends ServiceImpl<GcUserAnswerMapper, GcUserAnswer>
        implements GcUserAnswerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserAnswerServiceImpl.class);

    @Override
    public List<GcUserAnswer> getAnswerListByEventId(
            Integer eventId, Integer userId, Integer masterId) {
        return this.baseMapper.selectGetAnswerListByEventId(eventId, userId, masterId);
    }

    @Override
    public boolean saveUserAnswer(GcUserAnswer userAnswer) {
        return this.saveOrUpdate(userAnswer);
    }

    @Override
    public GcUserAnswer getMyEventAnswerByEventId(Integer eventId, Integer userId, Integer masterId) {
        QueryWrapper<GcUserAnswer> queryWrapper = new QueryWrapper<GcUserAnswer>();
        queryWrapper.eq("event_id", eventId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        return getOne(queryWrapper);
    }

    @Override
    public List<GcUserAnswer> getAllAnswerList(List<Integer> eventIds) {
        QueryWrapper<GcUserAnswer> queryWrapper = new QueryWrapper<>();
        if (eventIds == null || eventIds.size() == 0) {
            eventIds = new ArrayList<Integer>();
            eventIds.add(0);
        }
        queryWrapper.in("event_id", eventIds);
        return this.list(queryWrapper);
    }

    @Override
    public List<GcUserAnswer> getAllAnswerListByEventIds(List<Integer> eventIds, Integer studentId) {
        return this.baseMapper.getAllAnswerListByEventIds(eventIds, studentId);
    }

    @Override
    public List<GcUserAnswer> getAnswerListByEvent(
            List<Integer> eventIds, Integer masterId, Integer userId) {
        Integer type = TableConstant.gcEvent_eventType_freeType2;
        return this.baseMapper.getAnswerListByEvent(eventIds, masterId, userId, type);
    }

    @Override
    public List<GcUserAnswer> getMyAnswerListByEvent(
            List<Integer> eventIds, Integer masterId, Integer userId) {
        return this.baseMapper.getMyAnswerListByEvent(eventIds, masterId, userId);
    }
}
