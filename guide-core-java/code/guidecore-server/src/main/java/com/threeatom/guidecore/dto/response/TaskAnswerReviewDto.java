package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAnswerReviewDto {
    private double score;
    private String text;
    private UserDetailsDto owner;
    private OffsetDateTime creationTime;
}
