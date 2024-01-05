package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 商户管理员
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@ApiModel(value="GcManager对象", description="商户管理员")
public class GcManager implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    @JSONField(deserialize=false)
    @ApiModelProperty(value = "业务实例id")
    private Integer sysId;

    @ApiModelProperty(value = "门户ID")
    private Integer masterId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @JSONField(serialize = false)
    private String password;

    @ApiModelProperty(value = "用户层级:空是门户管理员 ，1是门户用户")
    private Integer level;

    @ApiModelProperty(value = "密码盐")
    @JSONField(serialize = false)
    private String salt;

    @ApiModelProperty(value = "状态")
    @JSONField(serialize = false)
    private Integer state;
    
    @ApiModelProperty(value = "名字前缀")
    private	String firstName;
    
    @ApiModelProperty(value = "名字后缀")
    private String lastName;

    @JSONField(deserialize=false,serialize = false)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JSONField(deserialize=false,serialize = false)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private Integer superAdminFlag;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSuperAdminFlag() {
        return superAdminFlag;
    }

    public void setSuperAdminFlag(Integer superAdminFlag) {
        this.superAdminFlag = superAdminFlag;
    }

    public Integer getSysId() {
		return sysId;
	}

	public void setSysId(Integer sysId) {
		this.sysId = sysId;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    
    

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    @Override
    public String toString() {
        return "GcManager{" +
                "id=" + id +
                ", sysId=" + sysId +
                ", masterId=" + masterId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", level=" + level +
                ", salt='" + salt + '\'' +
                ", state=" + state +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
