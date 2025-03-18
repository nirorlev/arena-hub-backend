package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@TableName("task_choices")
public class TaskChoice {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    private String content;

    @TableField(value = "order_number")
    private Integer order;

    private Boolean isDeleted = false;

    public TaskChoice(TaskChoice taskChoice) {
        this.id = taskChoice.getId();
        this.taskId = taskChoice.getTaskId();
        this.content = taskChoice.getContent();
        this.order = taskChoice.getOrder();
        this.isDeleted = taskChoice.getIsDeleted();
    }
}
