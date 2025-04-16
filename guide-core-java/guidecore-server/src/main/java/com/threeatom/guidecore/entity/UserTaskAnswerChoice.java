package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_task_answer_choices")
public class UserTaskAnswerChoice {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userTaskAnswerId;
    private Integer taskChoiceId;

    private Boolean isMarked;
    private Boolean isCorrect;
}

