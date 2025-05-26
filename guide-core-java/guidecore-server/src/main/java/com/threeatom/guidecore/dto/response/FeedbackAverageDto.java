package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackAverageDto extends FeedbackDto {
    private double averageRating;
    private int feedbacksCount;
}