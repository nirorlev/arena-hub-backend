package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户保存的内容
 * 
 * @author Kyle
 * @date 2021-10-17 21:50:01
 */
@Data
@ApiModel(value = "GcUserSaveContent",description = "用户保存的内容")
public class GcUserSaveContent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *  主键
	 */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 *  用户Id
	 */
	@ApiModelProperty(value = "用户Id")
	private Integer userId;

	@ApiModelProperty(value = "门户Id")
	private Integer masterId;
	/**
	 *  保存文件夹Id
	 */
	@ApiModelProperty(value = "保存文件夹Id")
	private Integer folderId;

	@ApiModelProperty(value = "文件id")
	private Integer fileId;

	/**
	 *  视频Id
	 */
	@ApiModelProperty(value = "视频Id")
	private Integer videoId;

	@TableField(exist = false)
	@JSONField(deserialize = false)
	@ApiModelProperty(value = "视频对象")
	private GcVideo video;
	
	/**
	 *  视频Id
	 */
	@ApiModelProperty(value = "课程Id")
	private Integer subId;

	@TableField(exist = false)
	@JSONField(deserialize = false)
	@ApiModelProperty(value = "课程对象")
	private GcSubject subject;
	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private Date updateTime;


	@TableField(exist = false)
	private SysFile videoFile;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private Date createTime;

	public GcUserSaveContent(){}

	public GcUserSaveContent(Integer userId,Integer masterId,Integer folderId){
		this.userId = userId;
		this.masterId = masterId;
		this.folderId = folderId;
	}
}