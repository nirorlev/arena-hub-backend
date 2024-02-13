package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserFabulous;
import com.threeatom.guidecore.mapper.GcUserFabulousMapper;
import com.threeatom.guidecore.service.GcUserFabulousService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author huangpei
 * @title: GcUserFabulousServiceImpl
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/29/02914:11
 */
@Service
public class GcUserFabulousServiceImpl extends ServiceImpl<GcUserFabulousMapper, GcUserFabulous>
        implements GcUserFabulousService {

    @Override
    public GcUserFabulous getUserFabulous(GcUserFabulous gcUserFabulous) {
        return this.baseMapper.getUserFabulous(gcUserFabulous);
    }

    @Override
    public Integer getEventFabulousNum(Integer eventId, Integer targetUserId, Integer commentId) {
        return this.baseMapper.getEventFabulousNum(eventId, targetUserId, commentId);
    }

    @Override
    public List<GcUserFabulous> getEventFabulousNumList(
            Integer eventId, List<Integer> targetUserIdList) {
        return this.baseMapper.getEventFabulousNumList(eventId, targetUserIdList);
    }
}
