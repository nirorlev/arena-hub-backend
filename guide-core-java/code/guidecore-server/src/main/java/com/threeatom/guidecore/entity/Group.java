package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "groups", autoResultMap = true)
public class Group {

    @TableId(value = "powtoon_group_code")
    private String powtoonGroupCode;

    private String name;
    private String parentPowtoonGroupCode;
    private Integer masterId;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
