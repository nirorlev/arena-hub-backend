package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user_task_answer_choices_pairings")
public class UserTaskAnswerChoicePairing {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userTaskAnswerId;

    private Integer taskChoiceId;

    private Integer pairedChoiceId;

    private Boolean isCorrect;
}
