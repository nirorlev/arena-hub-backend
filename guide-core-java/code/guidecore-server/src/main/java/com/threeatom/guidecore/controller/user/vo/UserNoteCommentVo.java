package com.threeatom.guidecore.controller.user.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author huangpei
 * @title: UserNodeCommentVo
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/27/02714:47
 */
@Data
public class UserNoteCommentVo {

    private Integer id;

    private String lastName;

    private String firstName;

    private String username;

    private Date sendingTime;

    private Integer userId;

    private Integer masterId;

    private Integer eventId;

    private String context;

    private Integer fileId;

    private SysFile resFile;

    @TableField(exist = false)
    private String userAvatarUrl;

    @TableField(exist = false)
    private SysFile userAvatarFile;

    @TableField(exist = false)
    private Integer readState;

    @TableField(exist = false)
    private Integer messageId;

    @TableField(exist = false)
    private Integer roleType;
}
