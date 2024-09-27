package com.threeatom.guidecore.dto.request;

import com.threeatom.guidecore.enums.SearchType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for searching")
public class SearchDto {
    @ApiModelProperty(notes = "Search term")
    private String searchName;

    @NotNull(message = "returnType is required")
    @ApiModelProperty(notes = "Search type")
    private Integer returnType;

    public SearchType getSearchType() {
        return SearchType.fromValue(returnType);
    }
}
