package com.threeatom.guidecore.util;

import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.dto.response.PaginationDto;
import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaginationUtil {
    public <T, U> PageableDto<T> createPageableDto(List<U> items, int totalCount, int pageSize,
                                                   Function<List<U>, List<T>> itemsConverter,
                                                   Function<List<U>, String> cursorExtractor) {
        return PageableDto.<T>builder()
            .results(itemsConverter.apply(items))
            .pagination(PaginationDto.builder()
                .count(totalCount)
                .pageSize(pageSize)
                .cursor(cursorExtractor.apply(items))
                .hasNextPage(hasNextPage(items, pageSize, totalCount))
                .build())
            .build();
    }

    private <T> boolean hasNextPage(List<T> items, int pageSize, long totalCount) {
        return false;
    }
}
