package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 视频下的event
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-18
 */
@ApiModel(value = "GcEvent对象", description = "视频下的event")
@Data
public class GcEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "视频ID")
    private Integer videoId;

    @ApiModelProperty(value = "类型 1=选择题，2=问答题，3=图片题，4=提示类信息（文字及图片）")
    private Integer eventType;

    @ApiModelProperty(value = "上传人")
    private Integer uploadUser;

    @ApiModelProperty(value = "上传人实体")
    @TableField(exist = false)
    private GcUser theUploadUser;

    @ApiModelProperty(value = "用户实体")
    @TableField(exist = false)
    private GcUser user;

    @ApiModelProperty(value = "上传类型，1=门户上传，2=老师添加")
    private Integer uploadType;

    @ApiModelProperty(value = "弹出时间点，单位秒")
    private Integer eventTime;

    @ApiModelProperty(value = "操作时间限制，单位秒")
    private Integer timeLimit;

    @ApiModelProperty(value = "问题标题")
    private String eventTitle;

    @ApiModelProperty(value = "事件从sys_file获取的链接")
    private Integer linkFileId;

    @ApiModelProperty(value = "链接文件对象")
    @TableField(exist = false)
    private SysFile linkFile;

    @ApiModelProperty(value = "事件链接视频gc_video的id")
    private Integer linkVideoId;

    @ApiModelProperty(value = "事件链接视频对象")
    @TableField(exist = false)
    private GcVideo linkVideo;

    @ApiModelProperty(value = "未来事件的前置事件id")
    private Integer futurePreEventId;

    @ApiModelProperty(value = "未来事件的前置事件")
    @TableField(exist = false)
    private GcEvent futurePreEvent;

    @ApiModelProperty(value = "未来事件延迟时间，单位小时")
    private Double futureDelayTime;

    @ApiModelProperty(value = "用户回答")
    @TableField(exist = false)
    private List<GcUserAnswer> answerList;

    @ApiModelProperty(value = "问题是否是当前用户上传")
    @TableField(exist = false)
    private Integer ifUploadByYourself;

    @ApiModelProperty(value = "本人回答")
    @TableField(exist = false)
    private GcUserAnswer myAnswer;

    @ApiModelProperty(value = "本人回答")
    @TableField(exist = false)
    private Integer myAnswerFlag;

    @ApiModelProperty(value = "额外的")
    private String ext;

    //    @TableField(value="ext",typeHandler = FastJsonArrayTypeHandler.class)
    //    private JSONObject extJson;

    @TableField(exist = false)
    @ApiModelProperty(value = "事件图片")
    private String eventImage;

    @TableField(exist = false)
    @ApiModelProperty(value = "事件图片")
    private SysFile eventImageFile;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    private Integer otherQuesImgId;

    @TableField(exist = false)
    private List<Integer> userIds;

    @ApiModelProperty(value = "回答问题时间")
    @TableField(exist = false)
    private Date answerUpdateTime;

    @TableField(exist = false)
    private String selfExt = "";

    @TableField(exist = false)
    private Object eventResMyNum;

    @TableField(exist = false)
    private Object eventResOthersNumUnRead;

    @TableField(exist = false)
    private Object eventResOthersNumRead;

    @TableField(exist = false)
    private Object eventAnswerState;

    @TableField(exist = false)
    private String answerJson;

    @TableField(exist = false)
    private Integer answerNum; // 回复数量

    @TableField(exist = false)
    private Integer resourceNum; // 本学生相关的上传的资源数量，本人上传+老师上传

    @TableField(exist = false)
    private Integer unReadMessages; // 未读消息数量

    @TableField(exist = false)
    private Integer thisUserAnsweredOrNot; // 当前用户是否回答了这个问题

    @TableField(exist = false)
    private String videoName;

    @TableField(exist = false)
    private String sub1Name;

    @TableField(exist = false)
    private String sub0Name;

    @TableField(exist = false)
    private Integer readState;

    @ApiModelProperty(value = "上一个回答的时间")
    @TableField(exist = false)
    private Date lastAnswerTime;

    @TableField(exist = false)
    private Integer subId;

    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private Integer subjectId;

    @TableField(exist = false)
    private Integer userAnserFabulousNum;

    private Integer answerMessageFlag;

    @TableField(exist = false)
    private Date answerCreateTime;

    @TableField(exist = false)
    private Integer myAnswerId;

    @TableField(exist = false)
    private Integer classId;

    @TableField(exist = false)
    private Integer oldVideoFileId;

    @TableField(exist = false)
    private List<GcEvent> updateEventList;

    @TableField(exist = false)
    private Integer fileId;
}
