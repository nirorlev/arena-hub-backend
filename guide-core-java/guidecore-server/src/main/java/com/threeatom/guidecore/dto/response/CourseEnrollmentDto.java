package com.threeatom.guidecore.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseEnrollmentDto extends BasicCourseDto {
    private Map<String, List<UserCourseEnrollmentDto>> userEnrollments;
}
