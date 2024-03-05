package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Course Assignment")
public class AssignCourseDto {
    @ApiModelProperty(notes = "The unique ID of the course to be assigned")
    private Integer courseId;
    @ApiModelProperty(notes = "The unique ID of the content group from which the course is to be assigned")
    private Integer contentGroupId;
    @ApiModelProperty(notes = "The unique ID of the user who assigns the course")
    private Integer createdByUserId;
    @ApiModelProperty(notes = "Specifies whether the course is mandatory or not. Default is false")
    private Boolean mandatory = false;
    @ApiModelProperty(notes = "The date when the course assignment is due", example = "2023-12-31")
    private LocalDateTime deadline;
}
