package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.mapper.GcUserAnswerMapper;
import com.threeatom.guidecore.mapper.GcUserMapper;
import com.threeatom.guidecore.mapper.GcUserVideoPlayMapper;
import com.threeatom.guidecore.service.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GcTeacherDataServiceImpl extends ServiceImpl<GcUserMapper, GcUser>
        implements GcTeacherDataService {

    @Autowired private GcUserVideoPlayMapper userVideoPlayMapper;

    @Autowired private GcUserAnswerMapper userAnswerMapper;

    @Autowired private GcUserAccessExtMapper userAccessExtMapper;

    @Override
    public JSONObject getStudentBehaviorChartsData(
            List<Integer> userIds,
            List<Integer> subIds,
            Integer masterId,
            String startDate,
            String endDate,
            Message message,
            Integer managerId) {

        if (userIds == null || userIds.size() == 0 || subIds == null || subIds.size() == 0) return null;

        JSONObject jsonObject = new JSONObject();

        List<Map<String, Object>> videoPlayHistoryMap =
                userVideoPlayMapper.showVideoPlayHistoryNumByStudentIdsAndSubject(
                        subIds, userIds, masterId, startDate, endDate);
        List<Map<String, Object>> loginNumMap =
                userAccessExtMapper.getUserLoginNum(userIds, startDate, endDate);
        List<Map<String, Object>> userAnswerNumMap =
                userAnswerMapper.getUserAnswerNum(userIds, startDate, endDate);
        List<Map<String, Object>> loginNumMapInCurrentPortal = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : loginNumMap) {
            if (masterId.equals(map.get("masterId"))) {
                loginNumMapInCurrentPortal.add(map);
            }
        }
        jsonObject.put("LoginsNum", loginNumMap);
        jsonObject.put("WatchesNum", videoPlayHistoryMap);
        jsonObject.put("AnswersNum", userAnswerNumMap);
        jsonObject.put("loginNumMapInCurrentPortal", JSON.toJSON(loginNumMapInCurrentPortal));
        jsonObject.put(
                "managerLoginNum", userAccessExtMapper.getByManagerId(managerId, startDate, endDate));

        return jsonObject;
    }
}
