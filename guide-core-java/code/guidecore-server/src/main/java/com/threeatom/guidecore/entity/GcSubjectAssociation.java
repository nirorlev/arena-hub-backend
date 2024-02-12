/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 课程门户关联Entity
 * @author Kyle
 * @version 2021-05-29
 */
@Data
public class GcSubjectAssociation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "门户id")
    private Integer masterId; // master_id

    @ApiModelProperty(value = "排序字段")
    private Integer order; // 排序字段

    @ApiModelProperty(value = "关联课程的gc_subject的id")
    private Integer subjectId; // 关联课程的gc_subject的id

    @ApiModelProperty(value = "状态: 0=隐藏，1或空=开")
    private Integer state; // 状态: 0=隐藏，1或空=开

    @ApiModelProperty(value = "关联课程的关联关系，1=导入import，2=别名alias")
    private Integer relationType; // 关联课程的关联关系，1=导入import，2=别名alias

    @JSONField(deserialize = false)
    private Date updateTime; // update_time

    @JSONField(deserialize = false)
    private Date createTime; // create_time
}
