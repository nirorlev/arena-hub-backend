package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiModel(value = "GcUserSaveFolder", description = "用户保存的视频的文件夹")
public class GcUserSaveFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @EqualsAndHashCode.Include
    private Integer id;

    @ApiModelProperty(value = "文件夹名称")
    @EqualsAndHashCode.Include
    private String name;

    @ApiModelProperty(value = "用户Id")
    @EqualsAndHashCode.Include
    private Integer userId;

    @ApiModelProperty(value = "用户Id")
    @EqualsAndHashCode.Include
    private Integer fileId;

    @ApiModelProperty(value = "状态: 0=隐藏，1或空=开")
    @EqualsAndHashCode.Include
    private Integer state;

    @ApiModelProperty(value = "门户id")
    @EqualsAndHashCode.Include
    private Integer masterId;

    private Date updateTime;

    private Date createTime;

    @TableField(exist = false)
    private String fullFileUrl;

    @TableField(exist = false)
    private Integer followNum;

    @TableField(exist = false)
    private Integer firstVideoFileId;

    private Boolean isPrivate = false;

    @TableField(exist = false)
    @EqualsAndHashCode.Include
    private Integer followFlag;

    @TableField(exist = false)
    private Integer videoNum;

    @TableField(exist = false)
    private GcUser user;

    @TableField(exist = false)
    private List<Integer> folderId;

    @TableField(exist = false)
    @EqualsAndHashCode.Include
    private Integer videoId;

    @TableField(exist = false)
    private Map<String, Boolean> permissions;

    @ApiModelProperty(value = "保存内容的list")
    @TableField(exist = false)
    private List<GcUserSaveContent> saveContentList;

    @TableField(exist = false)
    @EqualsAndHashCode.Include
    private OffsetDateTime subscriptionTime;

    @TableField(exist = false)
    private String snapshotUrl;

    @TableField(exist = false)
    private Integer videoSource;

    @TableField(exist = false)
    private OffsetDateTime lastContentUpdatedTime;
}
