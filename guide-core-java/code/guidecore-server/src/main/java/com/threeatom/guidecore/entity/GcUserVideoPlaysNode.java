package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * <p>
 * 视频的播放记录节点
 * </p>
 */
@ApiModel(value = "GcUserVideoPlaysNode对象", description = "视频的播放记录节点")
@Data
public class GcUserVideoPlaysNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "视频记录id")
    private Integer videoplayId;

    @ApiModelProperty(value = "节点开始时间")
    private Integer startTime;

    @ApiModelProperty(value = "节点结束时间")
    private Integer endTime;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "节点时长")
    private Integer sumTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频ID")
    private Integer videoId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    public GcUserVideoPlaysNode() {}

    public GcUserVideoPlaysNode(Integer startTime, Integer endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
