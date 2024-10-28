package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.AggregateByWithStep;
import com.threeatom.guidecore.enums.AnalyticsAggregation;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AggregateByWithStep
public class AnalyticsFilterDto extends DateRangeDto {

    private Long step;

    private String aggregateBy = AnalyticsAggregation.DATE.getCode();

    private List<Integer> videoIds = new ArrayList<>();

    public AnalyticsAggregation getAggregateBy() {
        return AnalyticsAggregation.fromCode(aggregateBy);
    }
}
