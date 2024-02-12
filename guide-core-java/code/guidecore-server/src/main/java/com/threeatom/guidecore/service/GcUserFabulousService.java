package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserFabulous;
import java.util.List;

/**
 * @author Administrator
 * @title: GcUserFabulousService
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/29/02914:09
 */
public interface GcUserFabulousService extends IService<GcUserFabulous> {

    GcUserFabulous getUserFabulous(GcUserFabulous gcUserFabulous);

    Integer getEventFabulousNum(Integer eventId, Integer targetUserId, Integer commentId);

    List<GcUserFabulous> getEventFabulousNumList(Integer eventId, List<Integer> targetIdList);
}
