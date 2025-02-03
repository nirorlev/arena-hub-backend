package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseEnrollmentService extends IService<CourseEnrollment> {
    void enrollToCourse(PortalUser portalUser, Integer courseId);

    CourseEnrollment getCourseEnrollment(PortalUser portalUser, Integer courseId);
}
