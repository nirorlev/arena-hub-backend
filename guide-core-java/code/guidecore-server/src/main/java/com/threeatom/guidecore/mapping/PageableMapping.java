package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.PageableDto;
import java.util.List;

public interface PageableMapping {
    <T> PageableDto<T> map(List<T> value, Integer pageNum, Integer pageSize);
}
