package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_task_answers")
public class UserTaskAnswer {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer ownerId;

    private Integer taskId;

    private Answer content;

    private Integer taskVersion;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    @TableField(exist = false)
    private Task task;
}
