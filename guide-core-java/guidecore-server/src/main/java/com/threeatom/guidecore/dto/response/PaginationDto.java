package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@ApiModel(description = "Data Transfer Object representing a pagination information")
public class PaginationDto {
    private Integer count;
    private Integer pageSize;
    private Boolean hasNextPage;
    private String cursor;
}
