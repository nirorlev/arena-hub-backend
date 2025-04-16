package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a group course assignment")
public class GroupCourseAssignmentDto {

    @ApiModelProperty(notes = "Course basic information")
    private BasicCourseDto course;

    @ApiModelProperty(notes = "Group basic information")
    private BasicGroupDto group;

    @ApiModelProperty(notes = "User basic information")
    private UserDetailsDto user;

    @ApiModelProperty(notes = "Whether the course is mandatory or not")
    private Boolean mandatory;

    @ApiModelProperty(notes = "The date when course assignment is due", example = "2023-12-31 00:00:00")
    private OffsetDateTime deadline;

    @ApiModelProperty(notes = "The date and time when the content group course assignment was last modified")
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}