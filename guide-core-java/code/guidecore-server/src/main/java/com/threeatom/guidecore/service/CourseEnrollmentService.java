package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.Optional;

public interface CourseEnrollmentService extends IService<CourseEnrollment> {
    void enrollToCourse(PortalUser portalUser, Integer courseId);

    Optional<CourseEnrollment> findCourseEnrollment(Integer courseId, PortalUser portalUser);
}
