package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing the source of a content group course assignment")
public class ContentGroupCourseAssignmentSourceDto {

    @ApiModelProperty(notes = "The content group from which the course was assigned")
    private ContentGroupAssignmentSourceDto contentGroup;

    @ApiModelProperty(notes = "The user who assigned the course")
    private ContentGroupAssignmentByUserDto user;
}