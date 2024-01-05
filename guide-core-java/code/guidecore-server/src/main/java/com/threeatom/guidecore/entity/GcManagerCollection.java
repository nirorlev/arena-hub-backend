package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 预注册客户收集
 * 
 * @author huangwenjun
 * @date 2021-10-12 15:04:10
 */
@Data
@ApiModel(value = "GcManagerCollection",description = "预注册客户收集")
public class GcManagerCollection implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *  
	 */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private String lastName;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private String firstName;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private String email;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private String phone;

	/**
	 *  更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	/**
	 *  创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	
}