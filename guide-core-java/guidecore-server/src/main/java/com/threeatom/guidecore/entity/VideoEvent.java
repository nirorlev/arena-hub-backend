package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.VideoEventType;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@TableName("video_events")
public class VideoEvent {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer videoId;
    private Integer videoTime;

    @TableField(value = "type", typeHandler = EnumTypeHandler.class)
    private VideoEventType type;

    @TableField(value = "order_number")
    private Integer order = 0;
    private Integer ownerId;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();

    @TableField(exist = false)
    private GcUser owner;

    @TableField(exist = false)
    private Task task;

    @TableField(exist = false)
    private Notification notification;
}

