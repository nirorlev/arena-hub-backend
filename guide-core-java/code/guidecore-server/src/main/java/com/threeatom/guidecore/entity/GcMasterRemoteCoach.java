package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.api.client.json.Json;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.common.mybatis.typehandler.FastJsonObjectTypeHandler;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 系统反馈
 *
 * @author huangpei
 * @Date 2021-10-26
 */
@Data
@TableName(autoResultMap = true)
public class GcMasterRemoteCoach implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer masterId;

    private Integer remoteType;

    private String remoteName;

    private BigDecimal price;

    private String remoteDesc;

    private String remotePlatform;

    private Integer remoteImgFileId;

    @TableField(
            value = "remote_available_call_length",
            typeHandler = FastJsonArrayTypeHandler.class
    )
    private JSONArray remoteAvailableCallLength;

    @TableField(
            value = "remote_available_date",
            typeHandler = FastJsonObjectTypeHandler.class
    )
    private JSONObject remoteAvailableDate;

    @TableField(
            value = "remote_available_time",
            typeHandler = FastJsonObjectTypeHandler.class
    )
    private JSONObject remoteAvailableTime;

    private Integer timezone;

    private Integer indefinitelyFlag;

    @TableField(exist = false)
    private SysFile imgFile;

    @TableField(exist = false)
    private String timeZoneValue;


    @TableField(exist = false)
    private Integer upcomingCalls;

    @TableField(exist = false)
    private String nextCall;

    @TableField(exist = false)
    private Integer selectedCallLength;

    @TableField(exist = false)
    private String day;
}
