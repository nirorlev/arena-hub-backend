package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "GcUserSaveFolder", description = "用户保存的视频的文件夹")
public class GcUserSaveFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文件夹名称")
    private String name;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "用户Id")
    private Integer fileId;

    @ApiModelProperty(value = "状态: 0=隐藏，1或空=开")
    private Integer state;

    @ApiModelProperty(value = "门户id")
    private Integer masterId;

    private Date updateTime;

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
    private GcUser user;

    @TableField(exist = false)
    private List<Integer> folderId;

    @TableField(exist = false)
    private Integer videoId;

    @ApiModelProperty(value = "保存内容的list")
    @TableField(exist = false)
    private List<GcUserSaveContent> saveContentList;

    @TableField(exist = false)
    private String snapshotUrl;

    @TableField(exist = false)
    private Integer videoSource;
}
