package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@ApiModel(description = "Data Transfer Object representing a pageable result")
public class PageableDto<T> {
    private List<T> results;
    private PaginationDto pagination;
}
