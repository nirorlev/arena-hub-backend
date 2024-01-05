package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Administrator
 * @title: GcExternalMessage
 * @projectName jeeplus
 * @description: TODO
 * @date 2022/8/1/00117:35
 */
@Data
public class GcExternalMessage {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String code;

    private Integer userId;

    private Integer type;

    private String message;

    private Date createTime;

    private Date updateTime;

    public GcExternalMessage(){

    }

    public GcExternalMessage(Integer id,String code,Integer userId,Integer type,String message,Date createTime,Date updateTime){
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public GcExternalMessage(String code,Integer type,String message,Date createTime,Date updateTime){
        this.code = code;
        this.type = type;
        this.message = message;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
