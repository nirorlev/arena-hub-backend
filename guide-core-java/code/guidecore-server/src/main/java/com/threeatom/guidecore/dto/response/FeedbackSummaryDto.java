package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackSummaryDto {
    private Double averageRating;
    private Integer feedbacksCount;
    private List<FeedbackDto> feedbacks;
}
