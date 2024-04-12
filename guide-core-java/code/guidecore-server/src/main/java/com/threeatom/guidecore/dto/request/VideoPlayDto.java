package com.threeatom.guidecore.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoPlayDto {
    private UUID sessionId;
    private Integer segmentId;
    private Integer startWatchTimeInSeconds;
    private Integer endWatchTimeInSeconds;
}
