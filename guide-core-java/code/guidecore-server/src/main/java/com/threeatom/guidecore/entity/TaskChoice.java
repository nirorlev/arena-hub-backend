package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("task_choices")
public class TaskChoice {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    private String content;

    @TableField(value = "order_number")
    private Integer order;

    private Boolean isDeleted = false;
}
