package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group info")
public class ContentGroupDto {

    @ApiModelProperty(notes = "The unique ID of the content group")
    private Integer id;

    @ApiModelProperty(notes = "The name of the content group")
    private String name;
}