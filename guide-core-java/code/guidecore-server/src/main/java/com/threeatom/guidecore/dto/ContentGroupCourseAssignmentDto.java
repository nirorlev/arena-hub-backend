package com.threeatom.guidecore.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentGroupCourseAssignmentDto {

    private Integer id;

    private String courseTitle;
    private String courseId;
    private String courseImageUrl;

    private Boolean mandatory;

    private ContentGroupCourseAssignmentSourceDto source;
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
