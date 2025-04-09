package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.dto.request.CourseSettingDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDetailsDto {
    private Integer id;
    private String name;
    private CourseTotalProgressDto progress = new CourseTotalProgressDto();
    private CourseSettingDto compliance = new CourseSettingDto();
}
