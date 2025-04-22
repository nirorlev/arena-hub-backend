package com.threeatom.guidecore.dto.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDto {
    private CourseProgressDetailsDto course;
    private Map<String, ProgressDetailsDto<SectionProgressDto>> sections = new HashMap<>();
    private Map<String, ProgressDetailsDto<ContentProgressDto>> content = new HashMap<>();
    private Map<String, ProgressDetailsDto<TaskProgressDto>> tasks = new HashMap<>();
}
