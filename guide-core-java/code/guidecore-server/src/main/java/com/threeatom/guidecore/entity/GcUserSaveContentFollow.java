package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "GcUserSaveContentFollow",description = "订阅")
public class GcUserSaveContentFollow implements Serializable {

	private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "文件夹名称")
	private Integer folderId;

	@ApiModelProperty(value = "用户Id")
	private Integer userId;

	@TableField(exist = false)
	private GcUserSaveFolder gcUserSaveFolder;

	private Date updateTime;

	private Date createTime;

}