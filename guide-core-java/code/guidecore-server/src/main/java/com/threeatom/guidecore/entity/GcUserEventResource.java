package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 事件资源文件
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Data
@ApiModel(value = "GcUserEventResource对象", description = "事件资源文件")
public class GcUserEventResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "问题，事件Id")
    private Integer eventId;

    @ApiModelProperty(value = "门户Id")
    private Integer masterId;

    @ApiModelProperty(value = "用户id")
    //    @JSONField(serialize = false)
    private Integer userId;

    @ApiModelProperty(value = "目标用户id")
    private Integer targetUserId;

    @ApiModelProperty(value = "目标用户id")
    @TableField(exist = false)
    private List<Integer> targetUserIdList;

    @ApiModelProperty(value = "时间节点")
    private Integer timeNode;

    @ApiModelProperty(value = "0:文字 1:图片2:视频3:声音")
    private Integer type;

    @ApiModelProperty(value = "回复于某个对应事件资源")
    private Integer targetId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "文件Id")
    private Integer fileId;

    //    @JSONField(serialize = false)
    private Date updateTime;
    //    @JSONField(serialize = false)
    private Date createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频的快照字段")
    private String snapshotUrl;

    @TableField(exist = false)
    private String fullFileUrl;

    @TableField(exist = false)
    private GcUser userDetail;

    @TableField(exist = false)
    private SysFile avatarFile;

    @TableField(exist = false)
    private SysFile resFile;

    // 作业本event回复资源用：
    @TableField(exist = false)
    private GcMasterMessage message;

    @TableField(exist = false)
    private GcUser thisUser;

    @TableField(exist = false)
    private GcUser targetUser;

    @TableField(exist = false)
    private Integer readState;

    @TableField(exist = false)
    private String videoName;

    @TableField(exist = false)
    private String sub1Name;

    @TableField(exist = false)
    private String sub0Name;

    @TableField(exist = false)
    private Integer unreadMessageNum;

    @TableField(exist = false)
    private Integer isTeacher; // 学生0 老师1
}
