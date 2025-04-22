package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("notifications")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer ownerId;

    private Integer videoEventId;

    // TODO: add enum when will be working on notifications
    private String type;

    private String content;

    private OffsetDateTime createdTime = OffsetDateTime.now();

    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
