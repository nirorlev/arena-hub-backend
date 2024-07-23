package com.threeatom.guidecore.dto.request;

import com.threeatom.guidecore.enums.AnalyticsType;
import com.threeatom.guidecore.enums.SortOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoListFilterDto extends DateRangeDto {

    private String query;
    private String sortBy;
    private String sortOrder = SortOrder.DESC.getCode();

    public AnalyticsType getSortBy() {
        return sortBy == null ? null : AnalyticsType.fromSortCode(sortBy);
    }

    public SortOrder getSortOrder() {
        return SortOrder.fromCode(sortOrder);
    }
}
