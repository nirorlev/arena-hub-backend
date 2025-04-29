package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.CourseFacade;
import com.threeatom.guidecore.service.CourseEnrollmentProgressService;
import com.threeatom.guidecore.service.CoursePreviewProgressService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseFacadeImpl implements CourseFacade {

    private final CourseEnrollmentProgressService courseEnrollmentProgressService;
    private final CoursePreviewProgressService coursePreviewProgressService;

    @Override
    public CourseProgressDto progress(Integer courseId, PortalUser portalUser,
                                      Map<String, Boolean> courseIdToPreviewMode) {
        if (isCoursePreview(courseId, courseIdToPreviewMode)) {
            return coursePreviewProgressService.courseProgress(courseId, portalUser);
        }

        return courseEnrollmentProgressService.courseProgress(courseId, portalUser);
    }

    private boolean isCoursePreview(Integer courseId, Map<String, Boolean> courseIdToPreviewMode) {
        return courseIdToPreviewMode.getOrDefault(String.valueOf(courseId), false);
    }
}
