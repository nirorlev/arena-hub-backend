package com.threeatom.guidecore.dto.response.analytic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoViewerVideoDetailDto {
    private double percentageViewed;
    private int totalViewTime;
    private int viewSessions;
}
