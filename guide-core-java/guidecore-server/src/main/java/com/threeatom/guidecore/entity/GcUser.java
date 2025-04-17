package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@ApiModel(value = "GcUser对象", description = "")
@Data
public class GcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "系统id")
    private Integer sysId;

    @ApiModelProperty(value = "pt用户")
    private Integer ptUser;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @JSONField(serialize = false)
    private String password;

    @ApiModelProperty(value = "盐")
    @JSONField(serialize = false)
    private String salt;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "用户信息表ID")
    private Integer infoId;

    private Integer powtoonUserId;

    @TableField(exist = false)
    private GcUserInfo info;

    @TableField(exist = false)
    private String lastName;

    @TableField(exist = false)
    private String firstName;

    @ApiModelProperty(value = "父id：老师的id")
    private Integer pid;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    // 临时判断该门户下有没有学生子账号
    @TableField(exist = false)
    private Integer studentFlag;

    @TableField(exist = false)
    private Integer subId;

    @TableField(exist = false)
    private Integer teacherFlag;

    @TableField(exist = false)
    private Integer subjectAdminFlag;

    @TableField(exist = false)
    private GcUserMessage userMessage;

    @TableField(exist = false)
    private Integer subjectId; // 课程id

    @TableField(exist = false)
    private Integer subjectUsers; // 课程下的用户数

    @TableField(exist = false)
    private Integer subjectMasterId; // 课程所在的门户

    @TableField(exist = false)
    private String thumbUrl; // pt用户头像

    @TableField(exist = false)
    private String ptEmail; // pt用户邮箱

    @TableField(exist = false)
    private List<GcAccess> accessList;

    @TableField(exist = false)
    private Integer accessId;

    @TableField(exist = false)
    private Integer avatarFileId;

    @TableField(exist = false)
    private String avatarFullFileUrl;

    @TableField(exist = false)
    private Date logInTime;

    @TableField(exist = false)
    private String groupName;

    public GcUser() {}

    public GcUser(Integer sysId, String username, String password, String salt, Integer infoId) {
        this.sysId = sysId;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.infoId = infoId;
    }
}
