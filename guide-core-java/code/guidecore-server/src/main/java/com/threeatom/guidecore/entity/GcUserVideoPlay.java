package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@ApiModel(value = "GcUserVideoPlay对象", description = "用户对视频的播放记录")
@Data
public class GcUserVideoPlay implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "视频id")
    private Integer videoId;

    @ApiModelProperty(value = "空间id")
    private Integer masterId;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "0:播放中1:看完")
    private Integer playState;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    private GcUserVideoPlaysNode videoPlaysNode;

    @TableField(exist = false)
    private List<GcUserVideoPlaysNode> videoPlaysNodes;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始观看时间")
    private Integer startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "观看结束时间")
    private Integer endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "观看时间")
    private Integer PlayTime;

    @TableField(exist = false)
    private Integer subjectId; // 课程id

    private Integer fileId;
}
