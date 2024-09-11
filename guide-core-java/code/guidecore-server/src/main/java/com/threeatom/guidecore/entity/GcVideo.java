package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@ApiModel(value = "GcVideo对象", description = "")
public class GcVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer originChannelId;

    private Integer originCourseId;

    @ApiModelProperty(value = "视频名称")
    private String videoName;

    @ApiModelProperty(value = "是否开启字幕")
    @TableField(exist = false)
    private Integer ifCaption;

    @ApiModelProperty(value = "原字幕语言")
    @TableField(exist = false)
    private String lang;

    @ApiModelProperty("目标字幕语言")
    @TableField(exist = false, value = "target_lang", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray targetLang = new JSONArray();

    @ApiModelProperty(value = "视频index，单个1级课程内唯一")
    private String videoNameIndex;

    @ApiModelProperty(value = "视频描述")
    private String videoDesc;

    @ApiModelProperty(value = "视频文件id")
    private Integer fileId;

    @ApiModelProperty(value = "视频文件描述")
    @TableField(exist = false)
    private SysFile videoFile;

    @TableField(exist = false)
    private Integer channelContentId;

    @TableField(value = "\"order\"")
    private Integer order;

    private String thumbnailUrl;

    @ApiModelProperty(value = "视频完整链接")
    @TableField(exist = false)
    private String videoFullUrl;

    @ApiModelProperty(value = "视频源1:oss	2:第三方youku或腾讯视频	3:screenrock")
    private Integer videoSource;

    @ApiModelProperty(value = "第三方视频的源地址")
    private String sourceUrl;

    @ApiModelProperty(value = "视频总时长")
    private Integer videoTime;

    @ApiModelProperty(value = "课程组织下面")
    private Integer subId;

    @TableField(exist = false)
    private Integer topSubId;

    @TableField(exist = false)
    private Integer childSubOrder;

    @JSONField(deserialize = false)
    private OffsetDateTime updateTime;

    @JSONField(deserialize = false)
    private OffsetDateTime createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论数")
    private int commentNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "点赞数")
    private int likeNum;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否点赞")
    private int isLiked;

    @TableField(exist = false)
    @ApiModelProperty(value = "一级课程id")
    private Integer subId0;

    @TableField(exist = false)
    @ApiModelProperty(value = "我的评论列表")
    private List<GcVideoComment> commentList;

    @TableField(exist = false)
    @ApiModelProperty(value = "问题事件list")
    private List<GcEvent> eventList;

    @TableField(exist = false)
    @ApiModelProperty(value = "问题事件list")
    private PageInfo<GcEvent> eventListPageInfo;

    @TableField(exist = false)
    @ApiModelProperty(value = "视频的快照字段") // ISysFileService.getVideoSnapshotUrl(SysFile, SysSystem)
    private String snapshotUrl;

    @TableField(exist = false)
    private String subjectName;

    @TableField(exist = false)
    private String topicName;

    @TableField(exist = false)
    private Integer watchStatus; // 是否已观看，1=是，-1=否

    @TableField(exist = false)
    private Integer ifOntime; // 是否准时观看，1=是，-1=否

    @TableField(exist = false)
    private Integer delayDays; // 延期天数

    @ApiModelProperty(value = "文件类型")
    @TableField(exist = false)
    private Integer fileTypeIndex;

    @TableField(exist = false)
    private Short completeStatus;

    @TableField(exist = false) // 上一次播放的视频信息
    private GcUserVideoPlaysNode gcUserVideoPlaysNode;

    /**
     * 1表示该视频已看完
     */
    @TableField(exist = false)
    private String playState;

    @TableField(exist = false)
    private Integer videoPlayNum;

    @TableField(exist = false)
    private String mostRecentlyDate;

    @TableField(exist = false)
    private Integer answeredNums; // 已回答问题数

    @TableField(exist = false)
    private Integer answeredSumNums; // 问题总数

    @TableField(exist = false)
    private Integer subjectSubId; // 已回答问题数

    @TableField(exist = false)
    private Integer eventNum;

    @TableField(exist = false)
    private Integer permissionId;

    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private Double starvalue;

    @TableField(exist = false)
    private BigDecimal taskProgress;

    @TableField(exist = false)
    private String fSubjectName;

    @TableField(exist = false)
    private Integer contentId;

    @TableField(exist = false)
    private Integer resourceNum;

    @TableField(exist = false)
    private Integer playNum;

    @TableField(exist = false)
    private Integer oldVideoFileId;

    @TableField(exist = false)
    private Integer currentStudentUserId;

    @TableField(exist = false)
    private PtChannel originChannel;

    @TableField(exist = false)
    private GcSubject originCourse;

    @TableField(exist = false)
    private Map<String, Boolean> permissions;

    @ApiModelProperty("课程tag标签")
    @TableField(value = "course_tags", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray courseTags = new JSONArray();

    // considered as public if everybody in the organization can access it
    public boolean isPublic() {
        if (originChannel != null) {
            return originChannel.isPublic();
        }

        return originCourse.isPublic();
    }

    // considered as private if only owner can access it
    public boolean isPrivate() {
        if (originChannel != null) {
            return originChannel.isPrivate();
        }

        return originCourse.isPrivate();
    }
}
