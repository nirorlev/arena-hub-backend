package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseProgramDto {
    private Integer id;
    private String name;
    private List<CourseSectionDto> sections;
}
