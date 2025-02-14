package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group course assignment")
public class ContentGroupCourseAssignmentDto extends GroupCourseAssignmentDto {

    @ApiModelProperty(notes = "The unique ID of the content group course assignment")
    private Integer id;
}