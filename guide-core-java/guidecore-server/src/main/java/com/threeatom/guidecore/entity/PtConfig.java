package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class PtConfig implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer masterId;
    private String pageHeader;
    private String pageBody;
    private Date createTime;
    private Date updateTime;
}
