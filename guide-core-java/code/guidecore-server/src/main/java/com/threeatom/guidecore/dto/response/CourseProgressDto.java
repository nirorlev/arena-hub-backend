package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDto {
    private CourseProgressDetailsDto course;
    private Map<String, ProgressDetailsDto<SectionProgressDto>> sections;
    private Map<String, ProgressDetailsDto<ProgressDto>> content;
    private Map<String, ProgressDetailsDto<TaskProgressDto>> tasks;
}
