package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.system.entity.SysFile;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-19
 */
@Data
@ApiModel(value="GcResource对象", description="")
public class GcResource implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "视频id")
    private Integer videoId;

    @ApiModelProperty(value = "文件名称")
    private String resourceName;

    @ApiModelProperty(value = "文件id")
    private Integer fileId;
    
    @ApiModelProperty(value = "文件实体类")
    @TableField(exist = false)
    private SysFile resFile;

    @ApiModelProperty(value = "资源类型 1:文件，2:引用")
    private Integer resourceType;

    @ApiModelProperty(value = "url链接")
    private String resUrl;
    
    @ApiModelProperty(value = "是否在视频结束后弹出，1=是")
    private Integer ifEndPopup;

    private Date createTime;

    private Date updateTime;

    @ApiModelProperty("课程tag标签")
    @TableField(
            value = "course_tags",
            typeHandler = FastJsonArrayTypeHandler.class,
            exist = false
    )
    private JSONArray courseTags = new JSONArray();

}
