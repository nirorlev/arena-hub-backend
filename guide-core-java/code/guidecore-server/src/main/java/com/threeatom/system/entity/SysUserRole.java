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

@ApiModel(
    value = "SysUserRole对象",
    description = ""
)
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    @TableId(
        value = "id",
        type = IdType.AUTO
    )
    private Integer id;
    @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("角色id")
    private Integer roleId;

    public SysUserRole() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String toString() {
        return "SysUserRole{id=" + this.id + ", userId=" + this.userId + ", roleId=" + this.roleId + "}";
    }
}
