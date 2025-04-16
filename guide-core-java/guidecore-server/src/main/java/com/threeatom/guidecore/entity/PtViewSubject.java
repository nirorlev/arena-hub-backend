package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "PtViewSubject", description = "")
@TableName(autoResultMap = true)
@Data
public class PtViewSubject {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer masterId;

    private Integer subjectId;

    private Integer userId;

    private Date createTime;

    private Date updateTime;
}
