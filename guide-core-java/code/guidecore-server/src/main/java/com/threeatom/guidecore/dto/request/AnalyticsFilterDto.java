package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.StartBeforeEndDate;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@StartBeforeEndDate
public class AnalyticsFilterDto {
    @NotNull(message = "Start cannot be null")
    private OffsetDateTime start;
    private OffsetDateTime end = OffsetDateTime.now();
    @NotNull(message = "Step cannot be null")
    @Positive(message = "Step must be positive number")
    private Long step;
    private boolean trendOnly;
}
