package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for passing ids")
public class IdsDto {
    @NotNull(message = "ids cannot be null")
    @NotEmpty(message = "ids cannot be empty")
    @ApiModelProperty(notes = "List of unique IDs", example = "[1, 2, 3]")
    private List<Integer> ids;
}
