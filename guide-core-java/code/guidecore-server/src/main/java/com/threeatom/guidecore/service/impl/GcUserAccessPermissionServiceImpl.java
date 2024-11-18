package com.threeatom.guidecore.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserAccessPermission;
import com.threeatom.guidecore.mapper.GcUserAccessPermissionMapper;
import com.threeatom.guidecore.service.GcUserAccessPermissionService;

@Service
public class GcUserAccessPermissionServiceImpl  extends ServiceImpl<GcUserAccessPermissionMapper, GcUserAccessPermission> implements GcUserAccessPermissionService {

}
