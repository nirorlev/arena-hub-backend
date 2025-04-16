package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDto {
    private Integer id;
    private Integer userId;
    private Integer rating;
    private String text;
    private OffsetDateTime creationDate;
    private boolean anonymous;
}