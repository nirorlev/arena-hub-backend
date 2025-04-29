package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.Map;

public interface CourseFacade {
    CourseProgressDto progress(Integer courseId, PortalUser portalUser, Map<String, Boolean> courseIdToPreviewMode);
}
