package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonObjectTypeHandler;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName(autoResultMap = true)
public class GcMasterRemoteCoachBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer remoteId;

    @TableField(value = "book_time", typeHandler = FastJsonObjectTypeHandler.class)
    private JSONObject bookTime;

    private Integer timeZone;

    private BigDecimal price;

    private Integer confirmFlag;

    private String url;

    private Integer userId;

    private Integer pastFlag;

    @TableField(exist = false)
    private String lastName;

    @TableField(exist = false)
    private String firstName;

    @TableField(exist = false)
    private String bookTimeString;

    @TableField(exist = false)
    private Integer masterId;

    @TableField(exist = false)
    private GcMasterRemoteCoach gcMasterRemoteCoach;
}
