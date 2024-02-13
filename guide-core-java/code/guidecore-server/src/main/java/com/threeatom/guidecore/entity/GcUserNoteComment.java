package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author huangpei
 * @title: GcUserNodeComment
 * @description: TODO
 * @date 2021/10/27/02714:28
 */
@Data
public class GcUserNoteComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 被评论用户的id
     */
    private Integer targetUserId;

    /**
     * 门户ID
     */
    private Integer masterId;

    /**
     * 事件id
     */
    private Integer eventId;

    /**
     * 回复内容
     */
    private String context;

    /**
     * 评论中文件id
     */
    private Integer fileId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频的快照字段，当type为2时可获取")
    private String snapshotUrl;
}
