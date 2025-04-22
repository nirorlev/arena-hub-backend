package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserEvent;
import com.threeatom.guidecore.mapper.GcUserEventMapper;
import com.threeatom.guidecore.service.GcUserEventService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class GcUserEventServiceImpl extends ServiceImpl<GcUserEventMapper, GcUserEvent>
        implements GcUserEventService {

    @Override
    public boolean updateUserEvents(Integer eid, List<Integer> userIds, Integer masterId) {

        try {
            QueryWrapper<GcUserEvent> queryWrapper = new QueryWrapper<GcUserEvent>();
            queryWrapper.eq("event_id", eid);
            List<GcUserEvent> list = this.list(queryWrapper);

            // 用于存放执行标志
            HashMap<Integer, String> saveMap = new HashMap<>();

            for (int i = 0; i < list.size(); i++) {
                // 默认设置旧数据为待删除标志
                saveMap.put(list.get(i).getUserId(), "D");
            }

            for (int i = 0; i < userIds.size(); i++) {
                // 该问题选择的用户已存在，则设置为保留标记，不存在则设置为新增标记
                if (saveMap.get(userIds.get(i)) != null) {
                    saveMap.put(userIds.get(i), "S");
                } else {
                    saveMap.put(userIds.get(i), "A");
                }
            }

            // 按照标志执行操作
            for (Integer userId : saveMap.keySet()) {
                if (saveMap.get(userId).equals("A")) {
                    this.save(new GcUserEvent(userId, eid, masterId));
                } else if (saveMap.get(userId).equals("D")) {
                    queryWrapper.eq("user_id", userId);
                    this.remove(queryWrapper);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Map<Integer, Object> selectUserEvents(List<Integer> userIds) {
        return baseMapper.selectUserEvents(userIds);
    }
}
