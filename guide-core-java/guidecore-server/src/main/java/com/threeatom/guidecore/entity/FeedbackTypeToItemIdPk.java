package com.threeatom.guidecore.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.EnumTypeHandler;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackTypeToItemIdPk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(value = "item_type", typeHandler = EnumTypeHandler.class)
    private FeedbackItemType itemType;
    private Integer itemId;
}
