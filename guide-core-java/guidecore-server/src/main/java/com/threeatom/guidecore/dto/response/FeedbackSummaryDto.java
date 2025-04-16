package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackSummaryDto {
    private Double averageRating = 0.0;
    private Integer feedbacksCount = 0;
    private List<FeedbackDto> feedbacks;
}
