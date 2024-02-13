//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "SysRole对象", description = "")
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("权限范围")
    private Integer sysId;

    @ApiModelProperty("角色名")
    private String roleName;

    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    @ApiModelProperty("是否超级管理员")
    private Integer superAdmin;

    @ApiModelProperty("说明")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public SysRole() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysId() {
        return this.sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return this.roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Integer getSuperAdmin() {
        return this.superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
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
        return "SysRole{id="
                + this.id
                + ", sysId="
                + this.sysId
                + ", roleName="
                + this.roleName
                + ", roleKey="
                + this.roleKey
                + ", remark="
                + this.remark
                + ", createTime="
                + this.createTime
                + ", updateTime="
                + this.updateTime
                + "}";
    }
}
