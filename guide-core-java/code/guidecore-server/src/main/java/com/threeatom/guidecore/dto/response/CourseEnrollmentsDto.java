package com.threeatom.guidecore.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseEnrollmentsDto {
    private List<UserDetailsDto> users;
    private Map<String, CourseEnrolmentDto> courses;
}
