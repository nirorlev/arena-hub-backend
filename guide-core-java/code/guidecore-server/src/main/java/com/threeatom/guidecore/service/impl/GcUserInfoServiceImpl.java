package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserInfo;
import com.threeatom.guidecore.mapper.GcUserInfoMapper;
import com.threeatom.guidecore.service.GcUserInfoService;
import org.springframework.stereotype.Service;

@Service
public class GcUserInfoServiceImpl extends ServiceImpl<GcUserInfoMapper, GcUserInfo>
        implements GcUserInfoService {}
