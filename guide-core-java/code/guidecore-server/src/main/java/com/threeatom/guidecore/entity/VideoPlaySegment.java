package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@TableName(value = "video_play_segment")
public class VideoPlaySegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private VideoPlaySegmentId id;

    @TableField(exist = false)
    private Integer startWatchTimeInSeconds;

    @TableField(exist = false)
    private Integer endWatchTimeInSeconds;

    @TableField(exist = false)
    private VideoPlaySession session;

    private OffsetDateTime createTime = OffsetDateTime.now();
    private OffsetDateTime updateTime = OffsetDateTime.now();
}
