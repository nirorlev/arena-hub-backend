package com.threeatom.guidecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@TableName(value = "content_group_channel_subscription", autoResultMap = true)
public class ContentGroupChannelSubscription implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer contentGroupId;
    private Integer channelId;
    private Integer createdByUserId;
    private Boolean autoSubscribe = false;

    @TableField(exist = false)
    private PtChannel channel;
    @TableField(exist = false)
    private GcUser createdBy;
    @TableField(exist = false)
    private GcAccess contentGroup;

    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime modifiedDate = OffsetDateTime.now();
}
