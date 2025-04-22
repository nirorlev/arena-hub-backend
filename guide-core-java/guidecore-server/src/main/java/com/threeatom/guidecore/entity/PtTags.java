package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "ptTags", description = "")
@TableName(autoResultMap = true)
@Data
public class PtTags {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer masterId;

    private Integer subjectId;

    private String tagText;

    @TableField(value = "\"type\"")
    private Integer type;

    @TableField(value = "\"order\"")
    private Integer order;

    private Integer videoId;

    private Date createTime;

    private Date updateTime;

    private Integer resourceId;

    private Integer channelId;

    private Integer fileId;
}
