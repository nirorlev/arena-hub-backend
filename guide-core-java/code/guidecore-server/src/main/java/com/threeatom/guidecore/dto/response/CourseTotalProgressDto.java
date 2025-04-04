package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseTotalProgressDto extends ProgressDto {
    private int completedSectionsCount;
    private int completedTasksCount;
    private boolean isCompliant;
}
