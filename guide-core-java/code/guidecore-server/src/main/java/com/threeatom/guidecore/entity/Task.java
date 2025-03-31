package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threeatom.common.mybatis.typehandler.TaskAnswerJsonTypeHandler;
import com.threeatom.common.mybatis.typehandler.TaskPropertiesJsonTypeHandler;
import com.threeatom.guidecore.enums.TaskType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"videoEvent", "version", "updatedByUser", "courseId", "createdTime", "updatedTime"})
@TableName(value = "tasks", autoResultMap = true)
public class Task {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer ownerId;
    private Integer updatedByUserId;

    private Integer videoEventId;
    private Integer courseId;

    @TableField(value = "type", typeHandler = EnumTypeHandler.class)
    private TaskType type;

    private String question;

    @TableField(typeHandler = TaskAnswerJsonTypeHandler.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Answer answer;

    private Integer retries = -1;
    private Boolean allowSkip = true;
    private Boolean isDeleted = false;

    @TableField(typeHandler = TaskPropertiesJsonTypeHandler.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TaskProperties properties;

    private Integer version;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    private OffsetDateTime updatedTime = OffsetDateTime.now();

    @TableField(exist = false)
    private List<TaskChoice> choices;

    @TableField(exist = false)
    @JsonIgnore
    private VideoEvent videoEvent;

    @TableField(exist = false)
    @JsonIgnore
    private GcUser updatedByUser;

    public Task(Task task, Answer answer, TaskProperties properties) {
        this.id = task.getId();
        this.ownerId = task.getOwnerId();
        this.updatedByUserId = task.getUpdatedByUserId();
        this.videoEventId = task.getVideoEventId();
        this.courseId = task.getCourseId();
        this.type = task.getType();
        this.question = task.getQuestion();
        this.answer = answer;
        this.properties = properties;
        this.version = task.getVersion();
        this.retries = task.getRetries();
        this.allowSkip = task.getAllowSkip();
        this.isDeleted = task.getIsDeleted();
        this.createdTime = task.getCreatedTime();
        this.updatedTime = task.getUpdatedTime();
        this.choices = task.getChoices() == null ? null : task.getChoices().stream()
            .map(TaskChoice::new)
            .collect(Collectors.toList());
    }
}

