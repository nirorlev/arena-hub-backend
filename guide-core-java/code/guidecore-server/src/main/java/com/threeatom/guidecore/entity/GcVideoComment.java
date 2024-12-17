package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "GcVideoComment对象", description = "视频评论")
public class GcVideoComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "视频id")
    private Integer videoId;

    @ApiModelProperty(value = "门户id")
    private Integer masterId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "视频评论")
    private String comment;

    @ApiModelProperty(value = "文件id")
    private Integer fileId;

    @ApiModelProperty(value = "被回复的信息id")
    private Integer replyCommentId;

    @ApiModelProperty(value = "主评论id")
    private Integer mainCommentId;

    @ApiModelProperty(value = "被回复人的id")
    private Integer targetUserId;

    @ApiModelProperty(value = "消息是否已读")
    @TableField(exist = false)
    private Integer readState;

    @ApiModelProperty(value = "消息id")
    @TableField(exist = false)
    private Integer messageId;

    @TableField(exist = false)
    private OffsetDateTime updateTime = OffsetDateTime.now();

    @TableField(exist = false)
    private OffsetDateTime createTime = OffsetDateTime.now();

    @TableField(exist = false)
    private GcUser author;

    @TableField(exist = false)
    private List<GcUserFabulous> userFabulousList;

    @TableField(exist = false)
    private String lastName;

    @TableField(exist = false)
    private String firstName;

    @TableField(exist = false)
    private String userAvatarUrl;

    @TableField(exist = false)
    private SysFile userAvatarFile;

    @TableField(exist = false)
    private SysFile commentFile;

    @TableField(exist = false)
    private Integer fabulousNum;

    @TableField(exist = false)
    private Integer commentNum;

    @TableField(exist = false)
    private String targetLastName;

    @TableField(exist = false)
    private String targetFirstName;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String targetUsername;

    @TableField(exist = false)
    private Integer isFabulous;
}
