package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@TableName(value = "fe_version_override", autoResultMap = true)
public class FeVersionOverride implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String version;

    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime modifiedDate = OffsetDateTime.now();
}
