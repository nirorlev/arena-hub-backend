package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.TaskType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerKeyDto {
    private TaskType type;
    private String answer;
}
