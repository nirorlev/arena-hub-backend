package com.threeatom.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "SysBusiness对象", description = "业务表，用来描述业务")
public class SysBusiness implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("业务id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("业务名称")
    @TableField("\"name\"")
    private String name;

    @ApiModelProperty("业务代码")
    @TableField("\"key\"")
    private String key;

    @ApiModelProperty("留言")
    private String remark;

    private Date createTime;
    private Date updateTime;

    public SysBusiness() {}

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

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return "SysBusiness{id="
                + this.id
                + ", name="
                + this.name
                + ", key="
                + this.key
                + ", remark="
                + this.remark
                + ", createTime="
                + this.createTime
                + ", updateTime="
                + this.updateTime
                + "}";
    }
}
