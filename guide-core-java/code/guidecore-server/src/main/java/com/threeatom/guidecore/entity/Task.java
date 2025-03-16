package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.TaskAnswerJsonTypeHandler;
import com.threeatom.guidecore.enums.TaskType;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@TableName(value = "tasks", autoResultMap = true)
public class Task {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer ownerId;
    private Integer updatedByUserId;

    private Integer videoEventId;

    @TableField(value = "type", typeHandler = EnumTypeHandler.class)
    private TaskType type;

    private String question;

    @TableField(typeHandler = TaskAnswerJsonTypeHandler.class)
    private Answer answer;

    private Integer retries = -1;
    private Boolean allowSkip = true;
    private Boolean isDeleted = false;

    private TaskProperties properties;

    private Integer version;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    private OffsetDateTime updatedTime = OffsetDateTime.now();

    @TableField(exist = false)
    private List<TaskChoice> choices;

    @TableField(exist = false)
    private VideoEvent videoEvent;
}

