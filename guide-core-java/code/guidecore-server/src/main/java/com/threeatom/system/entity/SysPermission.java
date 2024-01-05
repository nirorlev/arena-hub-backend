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
    value = "SysPermission对象",
    description = "角色权限表"
)
public class SysPermission implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("主键")
    @TableId(
        value = "id",
        type = IdType.AUTO
    )
    private Integer id;
    @ApiModelProperty("角色id")
    private Integer roleId;
    @ApiModelProperty("模块id")
    private Integer moduleId;
    @ApiModelProperty("权限功能字符")
    private String permCode;

    public SysPermission() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getPermCode() {
        return this.permCode;
    }

    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }

    public String toString() {
        return "SysPermission{id=" + this.id + ", roleId=" + this.roleId + ", moduleId=" + this.moduleId + ", permCode=" + this.permCode + "}";
    }
}
