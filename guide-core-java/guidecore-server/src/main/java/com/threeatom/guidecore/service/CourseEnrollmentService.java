package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.UpdateEnrollmentDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.PortalUser;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CourseEnrollmentService extends IService<CourseEnrollment> {
    UserCourseEnrollmentDto enrollToCourse(Integer courseId, PortalUser portalUser);

    Optional<CourseEnrollment> findActiveCourseEnrollment(Integer courseId, PortalUser portalUser);

    CourseEnrollmentsDto courseEnrollments(Integer courseId, String users, OffsetDateTime startDate,
                                           OffsetDateTime endDate, PortalUser portalUser);

    CourseEnrollmentsDto courseEnrollments(String usersFlag, OffsetDateTime startDate, OffsetDateTime endDate,
                                           PortalUser portalUser);

    UserCourseEnrollmentDto courseEnrollment(Long enrollmentId, PortalUser portalUser);

    UserCourseEnrollmentDto updateCourseEnrollment(Long enrollmentId, UpdateEnrollmentDto updateEnrollmentDto,
                                                   PortalUser portalUser);

    CourseEnrollment getActiveEnrollment(Integer courseId, Integer userId);

    List<CourseEnrollment> courseEnrollments(Set<Integer> courseIds);
}
