package com.threeatom.guidecore.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSettingDto {
    @Max(value = 100, message = "courseContentStudyPercentage should be less than or equal to 100")
    @Min(value = 0, message = "courseContentStudyPercentage should be greater than or equal to 0")
    private Integer courseContentStudyPercentage = 0;

    @Max(value = 100, message = "singleVideoViewPercentage should be less than or equal to 100")
    @Min(value = 0, message = "singleVideoViewPercentage should be greater than or equal to 0")
    private Integer singleVideoViewPercentage = 0;

    @Max(value = 100, message = "tasksGradePercentage should be less than or equal to 100")
    @Min(value = 0, message = "tasksGradePercentage should be greater than or equal to 0")
    private Integer tasksGradePercentage = 0;
}
