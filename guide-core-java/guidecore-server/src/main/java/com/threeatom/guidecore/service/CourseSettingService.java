package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.Optional;

public interface CourseSettingService extends IService<CourseSetting> {
    void save(Integer courseId, CourseSettingDto courseSetting, PortalUser portalUser);

    Optional<CourseSetting> findByCourseId(Integer courseId);

    void update(Integer courseId, CourseSettingDto courseSetting, PortalUser portalUser);
}
