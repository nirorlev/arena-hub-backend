package com.threeatom.guidecore.dto.request;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyticsFilterDto extends DateRangeDto {
    @NotNull(message = "Step cannot be null")
    @Min(value = 0, message = "Step must be positive number or 0")
    private Long step;

    private List<Integer> videoIds = new ArrayList<>();
}
