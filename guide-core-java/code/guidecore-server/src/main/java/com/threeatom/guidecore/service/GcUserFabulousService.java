package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserFabulous;
import java.util.List;

public interface GcUserFabulousService extends IService<GcUserFabulous> {

    GcUserFabulous getUserFabulous(GcUserFabulous gcUserFabulous);

    Integer getEventFabulousNum(Integer eventId, Integer targetUserId, Integer commentId);

    List<GcUserFabulous> getEventFabulousNumList(Integer eventId, List<Integer> targetIdList);
}
