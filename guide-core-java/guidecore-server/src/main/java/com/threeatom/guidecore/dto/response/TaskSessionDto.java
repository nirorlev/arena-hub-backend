package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSessionDto {
    private UUID sessionId;
    private OffsetDateTime startTime;
    private OffsetDateTime clientTime;
    private Integer duration;
    private Integer answerId;
}
