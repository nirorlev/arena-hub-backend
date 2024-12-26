package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.CourseUser;
import com.threeatom.guidecore.mapper.CourseUserMapper;
import com.threeatom.guidecore.service.CourseUserService;
import org.springframework.stereotype.Service;

@Service
public class CourseUserServiceImpl extends ServiceImpl<CourseUserMapper, CourseUser> implements CourseUserService {
}
