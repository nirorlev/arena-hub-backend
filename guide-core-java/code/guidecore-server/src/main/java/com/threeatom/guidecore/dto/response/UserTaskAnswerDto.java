package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTaskAnswerDto {
    private Integer id;
    private String answer;
    private Integer taskVersion;
    private List<TaskAnswerReviewDto> reviews;
    private OffsetDateTime creationTime;
}
