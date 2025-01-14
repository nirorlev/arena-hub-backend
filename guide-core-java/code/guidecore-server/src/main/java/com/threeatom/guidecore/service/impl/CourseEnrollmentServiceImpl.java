package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.CourseEnrollmentMapper;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment> implements
    CourseEnrollmentService {

    private final GcSubjectService courseService;
    private final AuthorizationService authorizationService;

    @Override
    public void enrollToCourse(PortalUser portalUser, Integer courseId) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("No permission to view this course");
        }

        CourseEnrollment courseEnrollment = createCourseUser(portalUser, courseId);

        save(courseEnrollment);
    }

    private CourseEnrollment createCourseUser(PortalUser portalUser, Integer courseId) {
        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setCourseId(courseId);
        courseEnrollment.setUserId(portalUser.getUserId());

        return courseEnrollment;
    }
}
