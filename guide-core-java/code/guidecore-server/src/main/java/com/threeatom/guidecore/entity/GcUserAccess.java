package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
@ApiModel(value = "GcUserAccess对象", description = "")
public class GcUserAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "主站id")
    private Integer masterId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "门户用户id")
    private Integer managerId;

    @ApiModelProperty(value = "用户实体")
    @TableField(exist = false)
    private GcUser user;

    @ApiModelProperty(value = "访问权限id")
    private Integer accessId;

    @ApiModelProperty(value = "主站")
    @TableField(exist = false)
    private GcMaster master;

    @ApiModelProperty(value = "访问权限id")
    @TableField(exist = false)
    private GcAccess access;

    @ApiModelProperty(value = "删除状态")
    private Integer state;

    @ApiModelProperty(value = "父级")
    private String parentCode;

    @JSONField(deserialize = false, serialize = false)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JSONField(deserialize = false, serialize = false)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    private Integer usersNumInPortal;

    @TableField(exist = false)
    private Date userLastActivityTime;

    @TableField(exist = false)
    private Integer joinType;

    @TableField(exist = false)
    private Date dateSignedUp;

    @TableField(exist = false)
    private String email;

    @TableField(exist = false)
    private String fileUrl;

    @TableField(exist = false)
    private String fileSaveType;

    @TableField(exist = false)
    private Integer portalNums;

    @TableField(exist = false)
    private List<GcMaster> masterList;

    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private GcUserAccessPermission gcUserAccessPermission;

    @TableField(exist = false)
    private Integer ifBuyMyYearlyPackage;

    @ApiModelProperty(value = "角色json列表")
    @TableField(value = "role_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray roleJson;
}
