package com.threeatom.guidecore.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSettingDto {
    @NotNull
    @Max(value = 100, message = "courseContentStudyPercentage should be less than or equal to 100")
    @Min(value = 0, message = "courseContentStudyPercentage should be greater than or equal to 0")
    private Integer courseContentStudyPercentage;

    @NotNull
    @Max(value = 100, message = "singleVideoViewPercentage should be less than or equal to 100")
    @Min(value = 0, message = "singleVideoViewPercentage should be greater than or equal to 0")
    private Integer singleVideoViewPercentage;
}
