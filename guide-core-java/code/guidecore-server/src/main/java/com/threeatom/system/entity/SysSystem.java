package com.threeatom.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "SysSystem对象", description = "系统实例")
public class SysSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("实例名称")
    private String name;

    @ApiModelProperty("业务id")
    private Integer businessId;

    @ApiModelProperty("业务实例")
    @TableField(exist = false)
    private SysBusiness business;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public SysSystem() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SysBusiness getBusiness() {
        return this.business;
    }

    public void setBusiness(SysBusiness business) {
        this.business = business;
    }

    public Integer getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String toString() {
        return "SysSystem{id="
                + this.id
                + "businessId="
                + this.businessId
                + ", name="
                + this.name
                + ", createTime="
                + this.createTime
                + ", updateTime="
                + this.updateTime
                + "}";
    }
}
