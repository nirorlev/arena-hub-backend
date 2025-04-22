package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.TaskAnswerJsonTypeHandler;
import java.time.OffsetDateTime;
import java.util.List;
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

    @TableField(typeHandler = TaskAnswerJsonTypeHandler.class)
    private Answer content;

    private Integer taskVersion;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    @TableField(exist = false)
    private GcUser owner;

    @TableField(exist = false)
    private Task task;

    @TableField(exist = false)
    private List<UserTaskAnswerReview> userTaskAnswerReviews;

    @TableField(exist = false)
    private UserTaskAnswerChoice userTaskAnswerSingleChoice;

    @TableField(exist = false)
    private List<UserTaskAnswerChoice> userTaskAnswerMultipleChoices;

    @TableField(exist = false)
    private List<UserTaskAnswerChoicePairing> userTaskAnswerChoicePairings;

    @TableField(exist = false)
    private List<UserTaskAnswerChoiceFillInBlank> userTaskAnswerChoiceFillInBlanks;
}
