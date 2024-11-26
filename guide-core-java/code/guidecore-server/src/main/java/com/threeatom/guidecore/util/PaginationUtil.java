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
        List<T> convertedItems = itemsConverter.apply(items);
        boolean hasNextPage = hasNextPage(convertedItems, pageSize);

        if (hasNextPage) {
            convertedItems = convertedItems.subList(0, pageSize);
        }

        return PageableDto.<T>builder()
            .results(convertedItems)
            .pagination(PaginationDto.builder()
                .count(totalCount)
                .pageSize(pageSize)
                .cursor(cursorExtractor.apply(items))
                .hasNextPage(hasNextPage)
                .build())
            .build();
    }

    private <T> boolean hasNextPage(List<T> items, int pageSize) {
        return items.size() > pageSize;
    }
}
