package com.threeatom.guidecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value="GcUserInfo", description="用户信息表")
@Data
public class GcUserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "名")
	private String lastName;

	@ApiModelProperty(value = "性")
	private String firstName;
	
	@ApiModelProperty(value = "性别")
	private Integer gender;
	
	@ApiModelProperty(value = "手机号")
	private String phone;

	@ApiModelProperty(value = "用户头像文件id")
	private Integer avatarFileId;

	@TableField(exist = false)
	private SysFile avatarFile;

	@TableField(exist = false)
	private String username;//邮箱

	@TableField(exist = false)
	private Integer userId;
}

