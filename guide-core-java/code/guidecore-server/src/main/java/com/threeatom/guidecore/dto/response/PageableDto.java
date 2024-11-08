package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@ApiModel(description = "Data Transfer Object representing a pageable object")
public class PageableDto<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private Boolean hasNextPage;
    private List<T> list;
}
