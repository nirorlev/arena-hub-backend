package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.response.analytic.AnalyticsCountDto;
import com.threeatom.guidecore.entity.GcUser;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;

public interface AnalyticsFacade {

    AnalyticsCountDto channelsCount(List<Integer> contentGroupIds, @NotNull OffsetDateTime start, OffsetDateTime end,
                                    Integer masterId, GcUser currentUser);
}
