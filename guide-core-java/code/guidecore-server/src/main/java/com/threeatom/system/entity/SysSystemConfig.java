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

@ApiModel(value = "SysSystemConfig对象", description = "")
public class SysSystemConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("实例id")
    private Integer sysId;

    @ApiModelProperty("自定义appid")
    private String sysAppid;

    @ApiModelProperty("小程序客户端凭证")
    private String weappSecret;

    public SysSystemConfig() {}

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

    public String getSysAppid() {
        return this.sysAppid;
    }

    public void setSysAppid(String sysAppid) {
        this.sysAppid = sysAppid;
    }

    public String getWeappSecret() {
        return this.weappSecret;
    }

    public void setWeappSecret(String weappSecret) {
        this.weappSecret = weappSecret;
    }

    public String toString() {
        return "SysSystemConfig{id="
                + this.id
                + ", sysId="
                + this.sysId
                + ", weappSecret="
                + this.weappSecret
                + "}";
    }
}
