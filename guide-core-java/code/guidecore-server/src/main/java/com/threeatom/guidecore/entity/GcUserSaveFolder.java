package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户保存的视频的文件夹
 * 
 * @author Kyle
 * @date 2021-10-17 21:50:01
 */
@Data
@ApiModel(value = "GcUserSaveFolder",description = "用户保存的视频的文件夹")
public class GcUserSaveFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *  主键
	 */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 *  文件夹名称
	 */
	@ApiModelProperty(value = "文件夹名称")
	private String name;

	/**
	 *  用户Id
	 */
	@ApiModelProperty(value = "用户Id")
	private Integer userId;

	/**
	 *  封面图片文件id
	 */
	@ApiModelProperty(value = "用户Id")
	private Integer fileId;
	/**
	 *  状态: 0=隐藏，1或空=开
	 */
	@ApiModelProperty(value = "状态: 0=隐藏，1或空=开")
	private Integer state;

	@ApiModelProperty(value = "门户id")
	private Integer masterId;
	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private Date updateTime;

	/**
	 *  
	 */
	@ApiModelProperty(value = "")
	private Date createTime;

	@TableField(exist = false)
	private String fullFileUrl;

	@TableField(exist = false)
	private Integer followNum;

	@TableField(exist = false)
	private Integer firstVideoFileId;

	private Integer ifPrivate;

	@TableField(exist = false)
	private Integer followFlag;

	@TableField(exist = false)
	private Integer videoNum;

	@TableField(exist = false)
	//作者
	private GcUser user;

	@TableField(exist = false)
	//作者
	private List<Integer> folderId;

	@TableField(exist = false)
	//作者
	private Integer videoId;

    @ApiModelProperty(value = "保存内容的list")
    @TableField(exist = false)
    private List<GcUserSaveContent> saveContentList;

	@TableField(exist = false)
	//缩略图
	private String snapshotUrl;

	@TableField(exist = false)
	private Integer videoSource;
}