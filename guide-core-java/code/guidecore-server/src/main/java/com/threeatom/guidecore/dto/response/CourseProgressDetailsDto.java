package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgressDetailsDto {
    private Integer id;
    private String name;
    private ProgressDto progress;
}
