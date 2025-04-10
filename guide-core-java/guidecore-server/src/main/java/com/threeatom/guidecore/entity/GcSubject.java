package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.common.mybatis.typehandler.FastJsonObjectTypeHandler;
import com.threeatom.guidecore.enums.CourseState;
import com.threeatom.system.entity.SysFile;
import com.threeatom.utils.data.TreeNodeEntity;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@TableName(autoResultMap = true)
public class GcSubject implements Serializable, TreeNodeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JSONField(deserialize = false)
    private Integer id2;

    private Integer masterId;

    @ApiModelProperty(value = "主题name")
    private String name;

    @ApiModelProperty(value = "课程index，单个门户内唯一")
    private String nameIndex;

    @ApiModelProperty(value = "课程语言")
    private String language;

    @ApiModelProperty(value = "课程语言")
    private Integer cateId;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "主题类型")
    private Integer type;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "主题Id") // 后续层级关联都用fid，后续将弃用subId
    @Deprecated
    private Integer subId;

    @TableField(value = "\"order\"")
    private Integer order;

    @ApiModelProperty(value = "subject科目图片id")
    private Integer subImgId;

    @ApiModelProperty(value = "subject科目图片文件")
    @TableField(exist = false)
    private SysFile subImgFile;

    @ApiModelProperty(value = "父级ID") // 后续层级关联都用fid，后续将弃用subId
    private Integer fid;

    @ApiModelProperty(value = "父级ID")
    private Integer aliasSubId;

    @JSONField(deserialize = false)
    @ApiModelProperty(value = "树状结构层")
    private Integer level;

    @JSONField(deserialize = false)
    private Date updateTime;

    @JSONField(deserialize = false)
    private Date createTime;

    // workbook接口中使用，主题下的视频list
    @TableField(exist = false)
    @ApiModelProperty(value = "主题下的视频list")
    private List<GcVideo> videoChildList;

    @ApiModelProperty(value = "是否需要课程介绍页面，1=需要，null=不需要")
    private Integer introOnOff;

    @ApiModelProperty(value = "首页样式id")
    private Integer templateId;

    @ApiModelProperty(value = "状态: 0=隐藏，1或空=开")
    private Integer state;

    @TableField(exist = false)
    private Boolean isPrivate;

    @ApiModelProperty(value = "课程标语")
    private String courseTagline;

    @ApiModelProperty("课程tag标签")
    @TableField(value = "course_tags", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray courseTags = new JSONArray();

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "其他课程导入该课程的密匙token")
    private String token;

    @ApiModelProperty(value = "该课程是否是公共课程，1=是，如是其他课程导入该课程不需要token")
    private Integer isPublic;

    @ApiModelProperty(value = "课程时长")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String cpdHours;

    @ApiModelProperty("课程开关 1开 0关")
    @TableField(exist = false)
    private Integer subjectShowFlag;

    @ApiModelProperty("总条数")
    @TableField(exist = false)
    private Integer total;

    @TableField(exist = false)
    private Integer subjectAssociationId;

    @TableField(exist = false)
    private Integer subjectAssociationOrder;

    @TableField(exist = false)
    private Integer subjectAssociationRelationType;

    @TableField(exist = false)
    private GcUserVideoPlay userVideoPlay;

    // 观看状态
    @TableField(exist = false)
    private Integer watchedStatus;

    // add by 20210815
    @TableField(exist = false)
    private Long subjectVideoDuration; // 课程视频时长，单位秒

    @TableField(exist = false)
    private List<GcUserVideoPlay> userVideoPlays; // 课程下的视频播放进度list

    @TableField(exist = false)
    private List<GcVideo> gcVideos; // 课程下的视频

    @TableField(exist = false)
    private List<GcSubject> subjects; // 一级课程下的二级课程

    @TableField(exist = false)
    private Short subjectCompleteStatus; // 一级课程下的二级课程完成状态

    @TableField(exist = false)
    private List<GcVideo> gcVideoCompletes; // 课程下的视频

    @TableField(exist = false)
    private Integer videoProgressPercent; // 视频进度百分比

    @TableField(exist = false)
    private Integer subjectUsers; // 课程下的参与人数

    @TableField(exist = false)
    private Double starValue; // 星级评价值

    @TableField(exist = false)
    private Long starUsers; // 星级评价总人数

    @TableField(exist = false)
    private Long commentNumber; // 评论总数

    @TableField(exist = false)
    private Long giveLike; // 点赞数

    @TableField(exist = false)
    private Short completeStatus; // 课程完成状态

    @TableField(exist = false)
    private Integer videosTotalNum; // 其下视频总数量;

    @TableField(exist = false)
    private Integer videosTotalLong; // 其下视频总时长;

    @TableField(exist = false)
    private Integer eventTotalNum; // 其下视频其下的总问题数event;

    @TableField(exist = false)
    private Integer answeredEventNum; // 其下视频其下的总问题的已回答数answer;

    @TableField(exist = false)
    private Long videoFinishedNum; // 课程下已观看完的视频

    @TableField(exist = false)
    private String snapshotUrl; // 快照

    @TableField(exist = false)
    private Integer lastVideoId;

    @TableField(exist = false)
    private Integer subPlayState;

    @TableField(exist = false)
    private SubjectTotals subjectTotals;

    @TableField(exist = false)
    private GcMaster gcMaster;

    @ApiModelProperty(value = "未来事件的前置事件")
    @TableField(exist = false)
    private GcEvent futurePreEvent;

    @TableField(exist = false)
    private GcSubjectAssociation gcSubjectAssociation;

    @ApiModelProperty(value = "被购买次数")
    @TableField(exist = false)
    private Integer orderNum;

    @ApiModelProperty(value = "音频总时长")
    @TableField(exist = false)
    private Integer audioTotalLong;

    @ApiModelProperty(value = "课程adminid")
    @TableField(exist = false)
    private Integer managerId;

    @ApiModelProperty(value = "当前用户是否已拥有")
    @TableField(exist = false)
    private Integer ownFlag;

    @ApiModelProperty(value = "tag的类型 1为主门户 2为导入的门户")
    @TableField(exist = false)
    private Integer tagType;

    @ApiModelProperty(value = "tag内容")
    @TableField(exist = false)
    private String tagText;

    @ApiModelProperty(value = "workbook上一次回答问题时间")
    @TableField(exist = false)
    private String lastActivityTime;

    @ApiModelProperty(value = "一级课程名字")
    @TableField(exist = false)
    private String sub0Name;

    @ApiModelProperty(value = "一级课程名字")
    @TableField(exist = false)
    private String sub0Id;

    @ApiModelProperty(value = "是否允许证书下载flag")
    private Integer certificatesFlag;

    @ApiModelProperty(value = "是否允许证书下载flag")
    @TableField(exist = false)
    private Integer enableCertificatesFlag;

    @ApiModelProperty(value = "用户id")
    @TableField(exist = false)
    private Integer userId;

    @ApiModelProperty(value = "用户id")
    @TableField(exist = false)
    private GcUser user;

    @ApiModelProperty(value = "用户信息")
    @TableField(exist = false)
    private GcUser userInfo;

    @ApiModelProperty(value = "进度")
    @TableField(exist = false)
    private BigDecimal totalPercent;

    @ApiModelProperty(value = "课程标题描述的字体颜色")
    private String color;

    @TableField(exist = false)
    private Integer answerFabulousNum;

    @TableField(exist = false)
    private Integer firstVideoId;

    @TableField(exist = false)
    private GcSubject firstTopic;

    @TableField(exist = false)
    private Integer subjectsSize;

    @TableField(exist = false)
    private String tagName;

    @ApiModelProperty("课程按tag排序")
    @TableField(value = "course_tags", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray subjectOrder;

    @TableField(value = "subdetail_img_id", typeHandler = FastJsonObjectTypeHandler.class)
    private JSONObject subdetail_img_id = new JSONObject();

    @TableField(exist = false)
    private String subDetailImgUrl;

    @TableField(exist = false)
    private Integer publicMasterId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer badgeId;

    private Integer cpdFlag;

    private String badgeContent;

    private Integer resourceNum;

    @ApiModelProperty(value = "进度")
    @TableField(exist = false)
    private BigDecimal percents;

    @ApiModelProperty(value = "所有tag")
    @TableField(exist = false)
    private List<String> allTags;

    @TableField(exist = false)
    private Integer tagId;

    /**
     * 创建人id
     */
    private Integer createUser;

    /**
     * 是否是pt课程
     */
    @ApiModelProperty(value = "是否是pt课程")
    @TableField(exist = false)
    private Integer isPtSubject;

    @TableField(exist = false)
    private List<Integer> accessIds;

    private Integer availableType;

    /**
     * 是否是must课程 0是 1否
     */
    @TableField(exist = false)
    private Integer isMustSubject;

    /**
     * 必须学习的组
     */
    @TableField(exist = false)
    private List<Integer> mustAccessIds;

    @TableField(exist = false)
    private Map<String, Boolean> permissions;

    /**
     * 是否选择全部,0是 1否
     */
    @TableField(exist = false)
    private Integer allPublished;

    /**
     * may是否选择全部,0是 1否
     */
    @TableField(exist = false)
    private Integer allPublishedMay;

    @TableField(exist = false)
    Integer currentStudentUserId;

    /**
     * 是否自己可见 0是 1否
     */
    @TableField(exist = false)
    private Integer isMyView;

    /**
     * 移动到草稿箱 0是
     */
    @TableField(exist = false)
    private Integer moveDrafts;

    @TableField(exist = false)
    private JSONArray mustJsonArray;

    @TableField(exist = false)
    private JSONArray mayJsonArray;

    /**
     * 0 to do 新发布的没有开始的must课程
     * 1 overdue 暂无
     * 2 completed 完成
     * 3 draft 草稿
     * 4 Earn a Certificate 有证书下载的课程
     */
    @TableField(exist = false)
    private Integer identifying;

    @TableField(exist = false)
    private List<Integer> identifyings;

    /**
     * 发布时间
     */
    private Date publishedTime;

    /**
     * 发布者id
     */
    private Integer publishedUserId;

    /**
     * 模式 0查看 1编辑
     */
    @TableField(exist = false)
    private Integer mode;

    /**
     * 不为空时反显是否是to-do课程
     */
    @TableField(exist = false)
    private Integer isToDo;

    public boolean isTopic() {
        return fid != null;
    }

    public Boolean getIsPrivate() {
        return CourseState.PRIVATE.getValue().equals(state);
    }

    public boolean isPublic() {
        return CourseState.PUBLIC.getValue().equals(state) || CourseState.CERTAIN_TEAMS.getValue().equals(state);
    }

    public boolean isPrivate() {
        return getIsPrivate();
    }
}
