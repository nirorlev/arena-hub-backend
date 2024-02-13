package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.service.GcUserAccessExtService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-25
 */
@Service
public class GcUserAccessExtServiceImpl extends ServiceImpl<GcUserAccessExtMapper, GcUserAccessExt>
        implements GcUserAccessExtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserAccessExtServiceImpl.class);

    @Override
    public List<Map<String, Object>> getUserLoginNum(
            List<Integer> userIds, String startDate, String endDate) {
        if (userIds == null || userIds.size() == 0) return null;

        return this.baseMapper.getUserLoginNum(userIds, startDate, endDate);
    }

    //    @Override
    //    public Integer updateBatchEmailTime(List<Integer> extIds) {
    //        return this.baseMapper.getAllUserLastLogin(14);
    //    }
}
