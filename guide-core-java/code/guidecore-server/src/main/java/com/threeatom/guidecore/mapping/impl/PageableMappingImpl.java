package com.threeatom.guidecore.mapping.impl;

import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.mapping.PageableMapping;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PageableMappingImpl implements PageableMapping {
    @Override
    public <T> PageableDto<T> map(List<T> value, Integer pageNum, Integer pageSize) {
        return PageableDto.<T>builder()
            .total(value.size())
            .pageNum(pageNum)
            .pageSize(pageSize)
            .list(value)
            .hasNextPage(value.size() > pageSize * (pageNum + 1))
            .build();
    }
}
