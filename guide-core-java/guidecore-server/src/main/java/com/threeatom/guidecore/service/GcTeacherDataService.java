package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;
import java.util.List;

public interface GcTeacherDataService {

    JSONObject getStudentBehaviorChartsData(
            List<Integer> userIds,
            List<Integer> subIds,
            Integer masterId,
            String startDate,
            String endDate,
            Message message,
            Integer managerId);
}
