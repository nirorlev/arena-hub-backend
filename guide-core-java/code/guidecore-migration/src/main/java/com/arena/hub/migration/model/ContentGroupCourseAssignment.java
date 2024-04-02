package com.arena.hub.migration.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ContentGroupCourseAssignment {
    private Integer id;

    private Integer contentGroupId;
    private Integer courseId;
    private Integer createdByUserId;

    private Boolean mandatory = false;
    private LocalDateTime deadline;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
