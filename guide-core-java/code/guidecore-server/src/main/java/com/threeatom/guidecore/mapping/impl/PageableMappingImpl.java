package com.threeatom.guidecore.mapping.impl;

import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.mapping.PageableMapping;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PageableMappingImpl implements PageableMapping {
    @Override
    public <T> PageableDto<T> map(List<T> list, Integer pageNum, Integer pageSize) {
        return PageableDto.<T>builder()
            .total(list.size())
            .pageNum(pageNum)
            .pageSize(pageSize)
            .list(paginatedList(list, pageNum, pageSize))
            .hasNextPage(list.size() > pageSize * (pageNum + 1))
            .build();
    }

    private <T> List<T> paginatedList(List<T> value, Integer pageNum, Integer pageSize) {
        return value.stream()
            .skip((long) pageNum * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
    }
}
