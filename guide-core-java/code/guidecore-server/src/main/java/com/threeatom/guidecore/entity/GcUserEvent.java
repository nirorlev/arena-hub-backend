package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;



@ApiModel(value="GcUserAccess对象", description="")
public class GcUserEvent implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "门户id")
    private Integer masterId;

    @ApiModelProperty(value = "事件id")
    private Integer eventId;
    
    @ApiModelProperty(value = "用户实体")
    @TableField(exist = false)
    private GcUser user;

    @ApiModelProperty(value = "删除状态")
    private Integer state;


    @JSONField(deserialize = false,serialize = false)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JSONField(deserialize = false,serialize = false)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public GcUser getUser() {
        return user;
    }

    public void setUser(GcUser user) {
        this.user = user;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public GcUserEvent(Integer userId, Integer eventId,Integer masterId) {
        this.userId = userId;
        this.eventId = eventId;
        this.masterId = masterId;
    }

    public GcUserEvent() {
    }
}
