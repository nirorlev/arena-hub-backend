package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@TableName(value = "feedbacks", autoResultMap = true)
public class Feedback {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer userId;

    @TableField(value = "item_type", typeHandler = EnumTypeHandler.class)
    private FeedbackItemType itemType;

    private Integer itemId;

    private Integer rating;
    private String content;
    private Boolean anonymous;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}


