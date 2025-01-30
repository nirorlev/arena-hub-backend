package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseSettingService extends IService<CourseSetting> {
    void save(Integer courseId, CourseSettingDto courseSetting, PortalUser portalUser);

    CourseSetting findByCourseId(Integer courseId);
}
