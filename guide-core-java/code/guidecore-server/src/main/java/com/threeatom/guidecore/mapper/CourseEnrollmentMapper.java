package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.CourseEnrollment;
import java.time.OffsetDateTime;
import java.util.List;

public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {
    CourseEnrollment findCourseEnrollment(Integer courseId);

    List<CourseEnrollment> getCourseEnrollments(Integer courseId);

    List<CourseEnrollment> getCourseEnrollmentsByUserId(Integer courseId, OffsetDateTime startDate,
                                                        OffsetDateTime endDate, Integer userId);
}
