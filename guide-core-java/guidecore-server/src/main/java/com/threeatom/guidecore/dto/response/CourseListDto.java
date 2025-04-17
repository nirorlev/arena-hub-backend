package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseListDto<T extends BasicCourseDto> {
    private List<T> courses;
}
