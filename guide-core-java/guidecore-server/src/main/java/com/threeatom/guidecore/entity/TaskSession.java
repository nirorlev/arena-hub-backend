package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("task_sessions")
public class TaskSession {

    @TableId
    private UUID id;

    private Integer taskId;
    private Integer userId;
    private Integer duration;

    private OffsetDateTime startTime;
    private OffsetDateTime clientTime;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
