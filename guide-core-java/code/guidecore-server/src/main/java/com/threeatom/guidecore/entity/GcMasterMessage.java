package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class GcMasterMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "发送用户的id")
    private Integer userId;

    @ApiModelProperty(value = "空间id")
    private Integer masterId;

    @ApiModelProperty(value = "接收用户的id")
    private Integer targetUserId;

    @ApiModelProperty(value = "事件类型")
    private Integer eventType;

    @ApiModelProperty(value = "事件gc_user_event_resource的id")
    private Integer resId;

    @ApiModelProperty(value = "问题回答gc_user_answer的id")
    private Integer userAnswerId;

    @ApiModelProperty(value = "日程gc_user_schedule的id")
    private Integer userScheduleId;

    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "已读状态")
    private Integer readState;

    @ApiModelProperty(value = "视频评论表gc_video_comment的id")
    private Integer videoCommentId;

    @ApiModelProperty(value = "作业本回复表gc_user_note_comment的id")
    private Integer userNoteCommentId;

    private Date updateTime;

    private Date createTime;
}
