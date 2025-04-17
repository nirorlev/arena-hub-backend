package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignedCourseDto extends CourseDto {
    private Boolean isMandatory;
    private OffsetDateTime deadline;

    public AssignedCourseDto(CourseDto courseDto) {
        this.setId(courseDto.getId());
        this.setTitle(courseDto.getTitle());
        this.setDescription(courseDto.getDescription());
        this.setThumbUrl(courseDto.getThumbUrl());

        this.setIsPublic(courseDto.getIsPublic());
        this.setIsPrivate(courseDto.getIsPrivate());
        this.setVideosCount(courseDto.getVideosCount());
        this.setVideosDuration(courseDto.getVideosDuration());
        this.setTasksCount(courseDto.getTasksCount());
        this.setTasksDuration(courseDto.getTasksDuration());
        this.setStudentsCount(courseDto.getStudentsCount());
        this.setAverageRating(courseDto.getAverageRating());
        this.setPermissions(courseDto.getPermissions());
    }
}
