package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@TableName(value = "video_play_session", autoResultMap = true)
public class VideoPlaySession implements Serializable {

    @TableId(value = "id")
    private UUID id;

    private Integer userId;
    private Integer videoId;
    private Integer masterId;

    private OffsetDateTime clientTime;
    private OffsetDateTime createTime = OffsetDateTime.now();
    private OffsetDateTime updateTime = OffsetDateTime.now();

    @TableField(exist = false)
    private GcUser user;

    @TableField(exist = false)
    private GcVideo video;

    @TableField(exist = false)
    private List<VideoPlaySegment> viewSegments;

    @TableField(exist = false)
    private GcMaster master;
}
