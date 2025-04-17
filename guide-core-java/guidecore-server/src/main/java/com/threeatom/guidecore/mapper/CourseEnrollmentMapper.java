package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.CourseEnrollment;
import java.time.OffsetDateTime;
import java.util.List;

public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
    CourseEnrollment findCourseEnrollment(Integer courseId, Integer userId, boolean active);

    List<CourseEnrollment> getCourseEnrollments(Integer courseId);

    List<CourseEnrollment> getCourseEnrollmentsByUserId(Integer courseId, OffsetDateTime startDate,
                                                        OffsetDateTime endDate, Integer userId);

    List<CourseEnrollment> getCourseEnrollmentsByMasterId(OffsetDateTime startDate, OffsetDateTime endDate,
                                                          Integer masterId);

    List<CourseEnrollment> getCourseEnrollmentsByUserAndMasterId(OffsetDateTime startDate, OffsetDateTime endDate,
                                                                 Integer userId, Integer masterId);

    CourseEnrollment getCourseEnrollmentById(Long id);

    int countDistinctUsersByCourse(Integer courseId);
}
