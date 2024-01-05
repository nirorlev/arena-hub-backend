package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 教师编辑的组权限
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-25
 */
@Data
@ApiModel(value="GcGroup对象", description="教师编辑的组权限")
@TableName(autoResultMap = true)
public class GcGroup implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "组名")
    private String name;

    @ApiModelProperty(value = "创建教师的身份")
    private Integer userAccessId;
    
    @TableField(exist = false)
    private GcAccess gcAccess;

    @ApiModelProperty(value = "创建空间")
    private Integer masterId;

    @ApiModelProperty(value = "组的课程权限")
    @TableField(value="sub_ids",typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray subIds;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "组的学生列表")
    @TableField(value="group_access_ids",typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray groupAccessIds;//gc_user_access的id
    
    @ApiModelProperty(value = "组分配日期")
    @TableField(value="schedule_date",typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray scheduleDate;

    @ApiModelProperty(value = "更新时间")
	@JSONField(deserialize=false)
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
	@JSONField(deserialize=false)
    private Date createTime;
    
    @ApiModelProperty(value = "初始化绑定code的gc_access的id")
    private Integer accessId;

    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private String codeName;

    @ApiModelProperty("邮件抄送人")
    @TableField(
            exist = false,
            typeHandler = FastJsonArrayTypeHandler.class
    )
    private JSONArray teacherList = new JSONArray();

    public GcGroup(){

    }
    public GcGroup(String name,Integer id){
        this.name = name;
        this.id = id;
    }

}
