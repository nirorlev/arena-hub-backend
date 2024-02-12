package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.validation.IntTypeConstraint;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * <p>
 * 用户对视频的操作，点赞 或者 收藏 等等
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-27
 */
@Data
@ApiModel(value = "GcUserVideoAction对象", description = "用户对视频的操作，点赞 或者 评价 等等 ")
public class GcUserVideoAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    //    @NotNull(message="videoId不可为空")
    @ApiModelProperty(value = "视频Id")
    private Integer videoId;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @IntTypeConstraint(
            values = {
                TableConstant.gcUserVideoAction_type_like1,
                TableConstant.gcUserVideoAction_type_rate2
            },
            message = "type必须是1，2")
    @NotNull(message = "type类型不可为空") @ApiModelProperty(value = "1:点赞2:评价3:星级评价")
    private Integer type;

    @Pattern(regexp = TableConstant.gcUserVideoAction_value_regexp, message = "value只能是1，2，3")
    @ApiModelProperty(value = "值")
    private String value;

    // add by 20210815
    private Double starValue; // 星级评价值

    @TableField(exist = false)
    private Integer subjectId; // 课程id

    private Integer subId;
    private String reviewTitle;
    private String reviewContent;

    private Integer fileId;

    private Date updateTime;

    private Date createTime;

    @TableField(exist = false)
    private Integer vid; // videoActionOld接口

    @TableField(exist = false)
    private Long subjectStarUsers; // 课程评价人数

    @TableField(exist = false)
    private Double subjectStarAvg; // 星级评价平均值

    @TableField(exist = false)
    private Integer userAvatarFileId; // 获取用户头像信息

    @TableField(exist = false)
    private SysFile avatarFile; // 获取用户头像信息

    @TableField(exist = false)
    private String userFirstName;

    @TableField(exist = false)
    private String userLastName;

    @TableField(exist = false)
    private Integer videoLikeNum;
}
