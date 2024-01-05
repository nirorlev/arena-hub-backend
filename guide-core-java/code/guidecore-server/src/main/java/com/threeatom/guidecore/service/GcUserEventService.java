package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcUserEvent;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频下的event 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-18
 */
public interface GcUserEventService extends IService<GcUserEvent> {
    boolean updateUserEvents(Integer eid ,List<Integer> userIds,Integer masterId );
    Map<Integer,Object> selectUserEvents(List<Integer> userIds);
}
