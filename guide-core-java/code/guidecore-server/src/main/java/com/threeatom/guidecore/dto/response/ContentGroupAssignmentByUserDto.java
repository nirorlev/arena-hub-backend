package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing the user who made the course assignment")
public class ContentGroupAssignmentByUserDto {

    @ApiModelProperty(notes = "The unique ID of the user who made the course assignment")
    private Integer id;

    @ApiModelProperty(notes = "The first name of the user who made the course assignment")
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user who made the course assignment")
    private String lastName;

    @ApiModelProperty(notes = "The profile photo URL of the user who made the course assignment")
    private String profilePhotoUrl;
}