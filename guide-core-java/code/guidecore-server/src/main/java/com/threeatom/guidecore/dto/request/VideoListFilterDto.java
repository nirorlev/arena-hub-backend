package com.threeatom.guidecore.dto.request;

import com.threeatom.guidecore.enums.AnalyticsType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoListFilterDto extends DateRangeDto {

    private String query;
}
