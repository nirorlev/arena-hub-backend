package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.StartBeforeEndDate;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[1-9]+[smdMy]$", message = "Step must match the pattern '^[1-9]+[smdMy]$'")
    private String step;
    private boolean trendOnly;
}
