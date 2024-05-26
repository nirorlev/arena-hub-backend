package com.threeatom.guidecore.dto;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbAnalyticsResultDto {
    private OffsetDateTime timeBucket;
    private double value;
}
