package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName(autoResultMap = true)
@NoArgsConstructor
@Getter
@Setter
public class FeatureToggle {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Integer masterId;
    private String value;
    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime modifiedDate = OffsetDateTime.now();
}
