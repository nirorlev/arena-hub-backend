package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionProgressDto extends ProgressDto {
    private int completedTasksCount;
}
