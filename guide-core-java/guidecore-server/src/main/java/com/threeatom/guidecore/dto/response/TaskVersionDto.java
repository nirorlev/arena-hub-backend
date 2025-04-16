package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskVersionDto {
    private OffsetDateTime creationTime;
    private UserDetailsDto createdBy;
    private TaskDto task;
}
