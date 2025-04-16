package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.CourseContentType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSectionContentDto {
    private Integer id;
    private String name;
    private CourseContentType type;
    private Integer duration;
    private List<CourseProgramTaskDto> tasks;
}
