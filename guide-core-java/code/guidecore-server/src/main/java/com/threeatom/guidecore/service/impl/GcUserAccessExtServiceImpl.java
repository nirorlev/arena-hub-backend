package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.constant.AccessRoleType;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcUserAccessExtMapper;
import com.threeatom.guidecore.mapper.GcUserAccessMapper;
import com.threeatom.guidecore.mapper.GcUserAccessPermissionMapper;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcGroupService;
import com.threeatom.guidecore.service.GcUserAccessExtService;
import com.threeatom.guidecore.service.GcUserAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-25
 */
@Service
public class GcUserAccessExtServiceImpl extends ServiceImpl<GcUserAccessExtMapper, GcUserAccessExt> implements GcUserAccessExtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserAccessExtServiceImpl.class);

    @Override
    public List<Map<String,Object>> getUserLoginNum(List<Integer> userIds, String startDate, String endDate) {
    	if(userIds==null|| userIds.size()==0)return null;
    	
        return this.baseMapper.getUserLoginNum(userIds,startDate,endDate);
    }

//    @Override
//    public Integer updateBatchEmailTime(List<Integer> extIds) {
//        return this.baseMapper.getAllUserLastLogin(14);
//    }
}
