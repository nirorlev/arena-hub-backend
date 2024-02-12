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
 * 知识库
 * </p>
 */
@ApiModel(value = "GcFaq", description = "知识库")
@Data
public class GcKnowledgebase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "title或者视频名字")
    private String name;

    @ApiModelProperty private Integer level;

    @ApiModelProperty private Integer fileId;

    @ApiModelProperty private Integer order;

    @ApiModelProperty private Integer delFlag;

    @ApiModelProperty private Date createTime;

    @ApiModelProperty private Date updateTime;

    @ApiModelProperty private Integer fid;

    @ApiModelProperty
    @TableField(exist = false)
    private String videoUrl;

    @ApiModelProperty
    @TableField(exist = false)
    private List<GcKnowledgebase> knowledgebase;
}
