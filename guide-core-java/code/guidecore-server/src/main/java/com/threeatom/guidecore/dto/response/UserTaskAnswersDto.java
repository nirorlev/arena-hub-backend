package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.TaskType;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTaskAnswersDto {
    private Map<String, UserTaskAnswerDetailDto> users;
    private TaskType taskType;
}
