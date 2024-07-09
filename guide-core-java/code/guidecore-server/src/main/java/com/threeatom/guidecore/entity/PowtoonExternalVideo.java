package com.threeatom.guidecore.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;


@Data
@TableName(value = "powtoon_external_video", autoResultMap = true)
public class PowtoonExternalVideo implements Serializable {
    @TableId(value="id", type=IdType.AUTO)
    private Integer id;

    private Integer sysFileId;

    private String externalId;

    private String origin;

    private String version;

    private String publicToken;
}
