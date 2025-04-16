package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseEnrollmentsDto {
    private Map<String, UserDetailsDto> users;
    private Map<String, CourseEnrollmentDto> courses;
}
