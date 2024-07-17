package com.threeatom.guidecore.dto.request;

import com.threeatom.common.validation.annotation.StartBeforeEndDate;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@StartBeforeEndDate
public class DateRangeDto {
    @NotNull(message = "Start cannot be null")
    private OffsetDateTime start;

    private OffsetDateTime end = OffsetDateTime.now();
}
