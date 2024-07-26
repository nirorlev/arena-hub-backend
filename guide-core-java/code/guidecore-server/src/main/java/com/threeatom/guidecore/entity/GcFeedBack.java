package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName(autoResultMap = true)
public class GcFeedBack implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer type;

    private String context;

    @TableField(value = "file_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray fileJson;

    private Date updateTime;

    private Date createTime;
}
