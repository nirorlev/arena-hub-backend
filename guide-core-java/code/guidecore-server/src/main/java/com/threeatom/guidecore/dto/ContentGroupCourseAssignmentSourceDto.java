package com.threeatom.guidecore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentGroupCourseAssignmentSourceDto {
    private ContentGroupAssignmentSourceDto contentGroup;
    private ContentGroupAssignmentByUserDto user;
}
