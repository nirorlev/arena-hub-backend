package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseFacade {
    CourseProgressDto progress(Integer courseId, PortalUser portalUser, boolean isPreview);
}
