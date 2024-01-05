package com.threeatom.guidecore.service;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.controller.Message;

public interface GcTeacherDataService {


	JSONObject getStudentBehaviorChartsData(List<Integer> userIds, List<Integer> subIds,Integer masterId,String startDate, String endDate, Message message,Integer managerId);
}
