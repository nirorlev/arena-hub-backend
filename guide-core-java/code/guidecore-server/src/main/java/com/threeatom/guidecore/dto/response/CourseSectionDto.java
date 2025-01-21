package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSectionDto {
    private Integer id;
    private String name;
    private List<CourseSectionContentDto> content;
}
