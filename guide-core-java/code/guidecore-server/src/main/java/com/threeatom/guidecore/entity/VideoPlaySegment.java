package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.IntegerRangeTypeHandler;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value = "video_play_segment", autoResultMap = true)
public class VideoPlaySegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private VideoPlaySegmentId id;

    @TableField(value = "segment", typeHandler = IntegerRangeTypeHandler.class)
    private NumberRange<Integer> segment;

    @TableField(exist = false)
    private VideoPlaySession session;

    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();
}
