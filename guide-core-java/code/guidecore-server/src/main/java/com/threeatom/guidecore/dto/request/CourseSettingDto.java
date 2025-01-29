package com.threeatom.guidecore.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSettingDto {
    private Integer courseCompletionPercentage;
    private Integer contentCompletionPercentage;
}
