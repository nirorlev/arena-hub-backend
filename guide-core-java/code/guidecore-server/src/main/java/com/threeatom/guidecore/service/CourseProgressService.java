package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseProgressService {
    CourseProgressDto courseProgress(Integer courseId, PortalUser portalUser);
}
