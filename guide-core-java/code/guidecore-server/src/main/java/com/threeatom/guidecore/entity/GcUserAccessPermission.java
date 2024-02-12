package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-25
 */
@ApiModel(value = "GcUserAccessPermission对象", description = "")
@Data
@TableName(autoResultMap = true)
public class GcUserAccessPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户userAccessId")
    private Integer userAccessId;

    @ApiModelProperty(value = "用户权限表")
    @TableField(value = "sub_permission", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray subPermission;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private Date createTime;

    @TableField(exist = false)
    private Integer managerId;

    @TableField(exist = false)
    private List<Integer> userIds;

    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private Integer masterId;

    @TableField(exist = false)
    private Integer codeId;

    @ApiModelProperty(value = "用户权限表")
    @TableField(value = "short_term_permission", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray shortTermPermission;

    @TableField(exist = false)
    private Date expired;

    @TableField(value = "channel_permission", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray channelPermission;

    @ApiModelProperty(value = "用户权限表")
    @TableField(exist = false, typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray permission;

    @ApiModelProperty(value = "必须学习的课程")
    @TableField(value = "must_subject_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray mustSubjectJson;

    @ApiModelProperty(value = "有权限的课程")
    @TableField(value = "may_subject_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray maySubjectJson;

    @TableField(value = "subscribe_permission", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray subscribePermission;

    @TableField(value = "follow_channel", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray followChannel;
}
