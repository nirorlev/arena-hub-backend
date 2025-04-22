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
@TableName("user_task_answer_reviews")
public class UserTaskAnswerReview {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userTaskAnswerId;

    private Double score;

    private Integer ownerId;

    private String content;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    @TableField(exist = false)
    private GcUser owner;
}
