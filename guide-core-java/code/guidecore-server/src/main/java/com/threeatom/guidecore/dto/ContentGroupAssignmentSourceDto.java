package com.threeatom.guidecore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing the content group which the course assignment is from")
public class ContentGroupAssignmentSourceDto {

    @ApiModelProperty(notes = "The unique ID of the content group which the assignment is from")
    private Integer id;

    @ApiModelProperty(notes = "The name of the content group which the assignment is from")
    private String name;
}