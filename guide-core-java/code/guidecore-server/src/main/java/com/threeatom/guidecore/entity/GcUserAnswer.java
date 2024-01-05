package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.controller.Message;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.common.mybatis.typehandler.FastJsonObjectTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 事件的用户问题回答
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Data
@ApiModel(value = "GcUserAnswer对象", description = "事件的用户问题回答")
@TableName(autoResultMap = true)
public class GcUserAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer eventId;

    @JSONField(deserialize = false)
    private Integer userId;

//    @TableField(typeHandler = FastJsonArrayTypeHandler.class)
    private String answerJson;

    private Integer masterId;

    @JSONField(deserialize = false)
    private Date updateTime;

    @JSONField(deserialize = false)
    private Date createTime;

    @TableField(exist = false)
    private String lastName;

    @TableField(exist = false)
    private String firstName;

    @TableField(exist = false)
    private String avatarUrl = "";

    @TableField(exist = false)
    public Integer saveType;
    
    @TableField(exist = false)
    private GcMasterMessage masterMessage;

    @TableField(exist = false)
    private Integer likeNum;

    @TableField(exist = false)
    private Integer commentNum;

    @TableField(exist = false)
    private Map<String,Object> answerMap;

    @TableField(exist = false)
    private Integer ifAnswerRight;

}
