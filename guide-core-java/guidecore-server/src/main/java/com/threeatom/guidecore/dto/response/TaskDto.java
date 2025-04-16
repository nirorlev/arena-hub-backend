package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    private Integer id;
    private Integer timestamp;
    private QuestionDto question;
    private Integer version;
    private Integer retries;
    private Boolean canSkip;
}