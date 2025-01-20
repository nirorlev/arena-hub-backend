package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDto {
    private CourseTotalProgressDto course;
    private Map<Integer, ProgressDto> sections;
    private Map<Integer, ProgressDto> content;
}
