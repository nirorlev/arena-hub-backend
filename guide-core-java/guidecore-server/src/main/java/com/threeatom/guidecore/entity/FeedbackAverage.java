package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@TableName(value = "feedbacks_average", autoResultMap = true)
public class FeedbackAverage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "item_type", typeHandler = EnumTypeHandler.class)
    private FeedbackItemType itemType;
    private Integer itemId;

    private Double averageRating;
    private Integer feedbacksCount;

    private OffsetDateTime updatedTime = OffsetDateTime.now();
}


