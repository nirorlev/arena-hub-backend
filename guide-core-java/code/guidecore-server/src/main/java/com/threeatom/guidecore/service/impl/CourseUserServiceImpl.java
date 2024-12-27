package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.CourseUser;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.CourseUserMapper;
import com.threeatom.guidecore.service.CourseUserService;
import com.threeatom.guidecore.service.GcSubjectService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseUserServiceImpl extends ServiceImpl<CourseUserMapper, CourseUser> implements CourseUserService {

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

        CourseUser courseUser = createCourseUser(portalUser, courseId);

        save(courseUser);
    }

    private CourseUser createCourseUser(PortalUser portalUser, Integer courseId) {
        CourseUser courseUser = new CourseUser();
        courseUser.setCourseId(courseId);
        courseUser.setUserId(portalUser.getUserId());

        return courseUser;
    }
}
