package com.threeatom.guidecore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group course assignment")
public class ContentGroupCourseAssignmentDto {

    @ApiModelProperty(notes = "The unique ID of the content group course assignment")
    private Integer id;

    @ApiModelProperty(notes = "The title of the course")
    private String courseTitle;

    @ApiModelProperty(notes = "The ID of the course")
    private String courseId;

    @ApiModelProperty(notes = "The URL of the course image")
    private String courseImageUrl;

    @ApiModelProperty(notes = "Whether the course is mandatory or not")
    private Boolean mandatory;

    @ApiModelProperty(notes = "The source of the content group course assignment")
    private ContentGroupCourseAssignmentSourceDto source;

    @ApiModelProperty(notes = "The date and time when the content group course assignment was last modified")
    private LocalDateTime modifiedDate = LocalDateTime.now();
}