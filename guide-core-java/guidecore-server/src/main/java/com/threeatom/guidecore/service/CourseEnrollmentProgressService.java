package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.CourseEnrollmentProgress;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseEnrollmentProgressService extends IService<CourseEnrollmentProgress> {
    CourseProgressDto courseProgress(Integer courseId, PortalUser portalUser);
}
