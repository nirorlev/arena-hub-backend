package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.validation.annotation.IntTypeConstraint;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.system.entity.SysFile;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GcUserVideoAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer videoId;

    private Integer userId;

    @IntTypeConstraint(
        values = {
            TableConstant.gcUserVideoAction_type_like1,
            TableConstant.gcUserVideoAction_type_rate2
        },
        message = "Type must be 1, 2")
    private Integer type;

    @Pattern(regexp = TableConstant.gcUserVideoAction_value_regexp, message = "value只能是1，2，3")
    private String value;

    private Double starValue;

    @TableField(exist = false)
    private Integer subjectId;

    private Integer subId;
    private String reviewTitle;
    private String reviewContent;

    private Integer fileId;

    private Integer contentId;

    private Date updateTime;

    private Date createTime;

    @TableField(exist = false)
    private Integer vid;

    @TableField(exist = false)
    private Long subjectStarUsers;

    @TableField(exist = false)
    private Double subjectStarAvg;

    @TableField(exist = false)
    private Integer userAvatarFileId;

    @TableField(exist = false)
    private SysFile avatarFile;

    @TableField(exist = false)
    private String userFirstName;

    @TableField(exist = false)
    private String userLastName;

    @TableField(exist = false)
    private Integer videoLikeNum;
}
