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
        boolean hasNextPage = hasNextPage(items, pageSize);
        List<U> paginatedList = getPaginatedList(items, pageSize, hasNextPage);

        return PageableDto.<T>builder()
            .results(itemsConverter.apply(paginatedList))
            .pagination(PaginationDto.builder()
                .count(totalCount)
                .pageSize(pageSize)
                .cursor(cursorExtractor.apply(paginatedList))
                .hasNextPage(hasNextPage)
                .build())
            .build();
    }

    private static <U> List<U> getPaginatedList(List<U> items, int pageSize, boolean hasNextPage) {
        if (hasNextPage) {
            return items.subList(0, pageSize);
        }

        return items;
    }

    private <T> boolean hasNextPage(List<T> items, int pageSize) {
        return items.size() > pageSize;
    }
}
