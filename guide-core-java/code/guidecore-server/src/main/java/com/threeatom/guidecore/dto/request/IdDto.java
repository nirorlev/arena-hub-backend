package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for passing single ID")
public class IdDto {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(notes = "ID of resource", example = "1")
    private Integer id;
}
