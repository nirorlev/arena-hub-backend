package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.TaskJsonTypeHandler;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("tasks_audit")
public class TaskAudit {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer taskId;
    private Integer createdByUserId;

    @TableField(typeHandler = TaskJsonTypeHandler.class)
    private Task previousTaskState;

    private Integer previousTaskVersion;

    private OffsetDateTime createdTime;

    @TableField(exist = false)
    private GcUser createdByUser;
}
