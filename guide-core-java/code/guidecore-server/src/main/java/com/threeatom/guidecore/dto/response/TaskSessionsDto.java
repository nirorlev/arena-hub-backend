package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSessionsDto {
    private List<TaskSessionDto> sessions;
}
