package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.entity.GcUserAccessExt;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.service.bll.GcUserAccessServiceBll;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-25
 */
public interface GcUserAccessExtService extends IService<GcUserAccessExt> {
    /**
     * 查询用户在某个时间段内登录的次数
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String,Object>> getUserLoginNum(List<Integer> userIds,String startDate,String endDate);

}
