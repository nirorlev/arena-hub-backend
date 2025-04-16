package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class GcUserNoteComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer targetUserId;

    private Integer masterId;

    private Integer eventId;

    private String context;

    private Integer fileId;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频的快照字段，当type为2时可获取")
    private String snapshotUrl;
}
