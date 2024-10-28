package com.threeatom.guidecore.dto.response.analytic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoViewerVideoDetailDto {
    private Double percentageViewed;
    private Integer totalViewTime;
    private Integer viewSessions;
}
