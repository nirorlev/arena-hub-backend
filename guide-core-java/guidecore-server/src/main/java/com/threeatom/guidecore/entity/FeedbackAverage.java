package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "feedbacks_average", autoResultMap = true)
public class FeedbackAverage {

    @TableId
    private FeedbackTypeToItemIdPk id;

    private Integer averageRating;
    private String feedbacksCount;

    private OffsetDateTime updatedTime;
}


