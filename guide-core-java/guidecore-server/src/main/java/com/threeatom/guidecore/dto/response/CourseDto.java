package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDto extends BasicCourseDto {
    private int videosCount;
    private int videosDuration;
    private int tasksCount;
    private int tasksDuration;
    private int studentsCount;
    private int activeStudentsCount;
    private double averageRating;
}
