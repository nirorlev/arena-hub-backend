package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 视频下的event
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-18
 */
@ApiModel(value = "GcCategory", description = "category")
@Data
public class GcCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "0为一级category，1为二级")
    private Integer level;

    @ApiModelProperty(value = "问题内容")
    private String categoryName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "order")
    private Integer order;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "父级Id")
    private Integer pid;

    @ApiModelProperty(value = "父级Id")
    private String fileName;

    @ApiModelProperty(value = "二级category")
    @TableField(exist = false)
    private List<GcCategory> gcCategory;
}
