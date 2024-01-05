package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.common.mybatis.typehandler.FastJsonObjectTypeHandler;
import com.threeatom.system.entity.SysFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 主站点实例
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
@Data
@ApiModel(value="GcMaster对象", description="主站点实例")
@TableName(autoResultMap = true)
public class GcMaster implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JSONField(deserialize=false)
    @ApiModelProperty(value = "管理员Id")
    private Integer managerId;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "站点标题")
    private String portalName;

    @ApiModelProperty(value = "站点context")
    private String context;
    
    @JSONField(deserialize=false)
    @ApiModelProperty(value = "站点状态")
    private Integer state;

    @ApiModelProperty(value = "logo文件")
    @TableField(exist=false)
    private SysFile logoFile;


    @ApiModelProperty(value = "门户价格")
    @TableField(exist=false)
    @TableId(value = "activate_size")
    private SysFile activateSize;


    @ApiModelProperty(value = "logo图片id")
    private Integer logoId;
    
    @ApiModelProperty(value = "logo完整链接")
    @TableField(exist=false)
    private String logoFullUrl;
    
    @ApiModelProperty(value = "介绍视频文件id")
    private Integer introVideoId;
    
    @ApiModelProperty(value = "null或1:本地，2:youku，3:screenRock")
    private Integer sourceType;
    
    @ApiModelProperty(value = "源地址  当第三方视频引用的时候")
    private String sourceUrl;
    
    @ApiModelProperty(value = "介绍视频文件")
    @TableField(exist=false)
    private SysFile introVideoFile;
    
    @ApiModelProperty(value = "外部日历链接")
    private String calendarLink;

    @ApiModelProperty(value = "brand描述")
    private String brandDescription;

    @ApiModelProperty(value = "brand主题颜色")
    private String color;

    @ApiModelProperty(value = "brand背景图id")
    private String brandImageId;

    @ApiModelProperty(value = "brand logo的类型 profile logo/lead photo")
    private String logoType;
    
    @TableField(exist = false)
    @ApiModelProperty(value = "视频的快照字段，当introVideoFile不为空时可获取")//ISysFileService.getVideoSnapshotUrl(SysFile, SysSystem)
    private String snapshotUrl;
    
    @ApiModelProperty(value = "模板id")
    private Integer templateId;
    
    @TableField(value="ext_var",typeHandler = FastJsonObjectTypeHandler.class)
    private JSONObject extVar;
    
    @TableField(value="intro_done_step",typeHandler = FastJsonObjectTypeHandler.class)
    private JSONObject introDoneStep;
 
    @JSONField(deserialize=false)
    private Date updateTime;

    @JSONField(deserialize=false)
    private Date createTime;


    @TableField(exist = false)
    private List<GcSubject> gcSubjectList;
    
    //临时判断该门户下有没有学生子账号
    @TableField(exist = false)
    private Integer studentFlag;

    private Integer profilePhotoId;

    @TableField(exist = false)
    private String profilePhotoFullFileUrl;

    private String brandTagline;

    private Integer answerShowFlag;

    private String connectedAccountId;

    private Integer splitFundsFlag;

    private Integer abilitySplitFundsFlag;

    @TableField(exist = false)
    private List<GcAccess> accessList;

    @ApiModelProperty("邮件抄送人")
    @TableField(
            value = "email_cc",
            typeHandler = FastJsonArrayTypeHandler.class
    )
    private JSONArray emailCc = new JSONArray();

    @TableField(exist = false)
    private JSONArray courseTags;

    @TableField(exist = false)
    private GcManager gcManager;

    @TableField(exist = false)
    private String managerUsername;

    @TableField(exist = false)
    private Integer userNumInPortal;

    @TableField(exist = false)
    private String searchFilter;

    @TableField(exist = false)
    private Date userLastActivityTime;

    @TableField(exist = false)
    private Date userLastLoginTime;

    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private List<Integer> channelIds;

    @TableField(exist = false)
    private GcUserAccess userAccess;

    @TableField(exist = false)
    private List<Integer> masterIds;

    @TableField(exist = false)
    private Integer accessId;

    @TableField(exist = false)
    private GcAccess freeAccessCode;

    private Integer adminLogoFileId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long boardId;

    @TableField(exist = false)
    private String adminLogoFullFileUrl;

    @ApiModelProperty("0显示userlist，1显示subjectadminlist")
    @TableField(exist = false)
    private Integer userAdminFlag;

    private Integer faviconLogoFileId;

    @TableField(exist = false)
    private String faviconFullFileUrl;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String redirectUrl;
}
