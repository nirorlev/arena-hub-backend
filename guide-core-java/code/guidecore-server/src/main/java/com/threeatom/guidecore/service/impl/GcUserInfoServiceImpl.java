package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserEvent;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.mapper.GcUserEventMapper;
import com.threeatom.guidecore.mapper.GcUserInfoMapper;
import com.threeatom.guidecore.service.GcUserEventService;
import com.threeatom.guidecore.service.GcUserInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;


@Service
public class GcUserInfoServiceImpl extends ServiceImpl<GcUserInfoMapper, GcUserInfo> implements GcUserInfoService {

}