package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import java.util.List;
import java.util.Map;

public interface GcUserAccessExtService extends IService<GcUserAccessExt> {
    List<Map<String, Object>> getUserLoginNum(
            List<Integer> userIds, String startDate, String endDate);
}
