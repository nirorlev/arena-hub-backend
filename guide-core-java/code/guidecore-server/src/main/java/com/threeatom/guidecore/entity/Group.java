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

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;
    private String powtoonGroupId;
    private String parentCode;
    private Integer masterId;

    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
