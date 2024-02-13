package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import lombok.Data;

/**
 * @author huangpei
 * @title: GcUserFabulous
 * @projectName guidecore
 * @description: TODO
 * @date 2021/10/29/02911:55
 */
@Data
public class GcUserFabulous {

    /**
     * 数据id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 事件id
     */
    private Integer eventId;

    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 被点赞人id
     */
    private Integer targetUserId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 评论id
     */
    private Integer commentId;

    public GcUserFabulous(Integer userId, Integer targetUserId, Integer videoId, Integer eventId) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.videoId = videoId;
        this.eventId = eventId;
    }

    public GcUserFabulous() {}
}
