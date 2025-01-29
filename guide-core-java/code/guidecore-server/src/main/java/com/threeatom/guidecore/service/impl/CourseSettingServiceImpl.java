package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.mapper.CourseSettingMapper;
import com.threeatom.guidecore.service.CourseSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseSettingServiceImpl extends ServiceImpl<CourseSettingMapper, CourseSetting>
    implements CourseSettingService {
}
