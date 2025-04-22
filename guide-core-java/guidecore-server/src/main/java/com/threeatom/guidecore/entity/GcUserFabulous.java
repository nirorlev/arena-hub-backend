package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import lombok.Data;

@Data
public class GcUserFabulous {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer eventId;

    private Integer videoId;

    private Integer targetUserId;

    private Date updateTime;

    private Date createTime;

    private Integer commentId;
}
