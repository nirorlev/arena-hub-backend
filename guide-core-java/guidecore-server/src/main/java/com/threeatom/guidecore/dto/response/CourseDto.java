package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDto extends BasicCourseDto {
    private Boolean isPublic;
    private Boolean isPrivate;
    private int videosCount;
    private int videosDuration;
    private int tasksCount;
    private int tasksDuration;
    private int studentsCount;
    private double averageRating;
    private Map<String, Boolean> permissions;
}
