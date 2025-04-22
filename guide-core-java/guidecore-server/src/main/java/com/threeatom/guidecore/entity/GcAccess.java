package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiModel(value = "GcAccess对象", description = "")
@TableName(autoResultMap = true)
public class GcAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @EqualsAndHashCode.Include
    private Integer id;

    @JSONField(deserialize = false)
    @EqualsAndHashCode.Include
    private Integer masterId;

    @ApiModelProperty(value = "角色类型")
    @EqualsAndHashCode.Include
    private Integer roleType;

    @ApiModelProperty(value = "code")
    @EqualsAndHashCode.Include
    private String code;

    @ApiModelProperty(value = "对应的adminId")
    private Integer adminId;

    @ApiModelProperty(value = "注册码的类型，空为用户注册码、1为门户注册码、2为免费code")
    private Integer codeType;

    @TableField(exist = false)
    private Integer userNum = 0;

    @JSONField(deserialize = false)
    private Date updateTime;

    @JSONField(deserialize = false)
    private Date createTime;

    @ApiModelProperty(value = "是否免费")
    private Integer freeFlag;

    @ApiModelProperty(value = "包名")
    private String packageName;

    @ApiModelProperty("价格以及价格period")
    @TableField(value = "package_price_period", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray packagePricePeriod = new JSONArray();

    @ApiModelProperty(value = "套餐描述")
    private String packageDescription;

    @ApiModelProperty(value = "套餐课程额外信息")
    private String packageAdditionalCourseInformation;

    @ApiModelProperty(value = "套餐封面文件id")
    private Integer packageImgId;

    @ApiModelProperty(value = "套餐展示开关 1开 0关")
    private Integer packageShowFlag;

    @ApiModelProperty(value = "当前用户购买状态,1为已拥有，0为未购买")
    @TableField(exist = false)
    private Integer ownedFlag;

    @ApiModelProperty(value = "package的封面的sysfile对象")
    @TableField(exist = false)
    private SysFile packageImgFile;

    @ApiModelProperty(value = "封面的完整路径")
    @TableField(exist = false)
    private String packageImgFullUrl;

    @ApiModelProperty(value = "套餐下所有课程的时长总和")
    @TableField(exist = false)
    private Long packageCourseTotalTime;

    @ApiModelProperty(value = "套餐下所有课程的平均星级")
    @TableField(exist = false)
    private Double packageCourseAvgStars;

    @ApiModelProperty(value = "套餐下星级不为空的课程的数量")
    @TableField(exist = false)
    private Integer times;

    @ApiModelProperty(value = "套餐下所有课程的评星人数")
    @TableField(exist = false)
    private Long packageCourseStarUsers;

    @ApiModelProperty(value = "套餐下的课程详情")
    @TableField(exist = false)
    private List<GcSubject> subjects;

    @ApiModelProperty(value = "免费code标志")
    @TableField(exist = false)
    private Integer defaultCodeFlag;

    @ApiModelProperty(value = "套餐下的课程详情")
    private Double packageLearningHours;

    @ApiModelProperty(value = "封面类型判断")
    @TableField(exist = false)
    private String fileType;

    @ApiModelProperty(value = "package视频")
    private String packageVideoFileId;

    @ApiModelProperty(value = "视频文件")
    @TableField(exist = false)
    private SysFile packageVideoFile;

    @ApiModelProperty(value = "视频文件封面")
    @TableField(exist = false)
    private String packageSnapShotUrl;

    @ApiModelProperty(value = "视频文件封面")
    @TableField(exist = false)
    private String packageVideoFullUrl;

    @ApiModelProperty(value = "user对应当前package的过期时间")
    @TableField(exist = false)
    private Date packageExpiredTime;

    @ApiModelProperty(value = "order排序")
    @TableField(value = "\"order\"")
    private Integer order;

    public List<GcAccess> getAccessList() {
        return accessList;
    }

    public void setAccessList(List<GcAccess> accessList) {
        this.accessList = accessList;
    }

    @ApiModelProperty(value = "子集")
    @TableField(exist = false)
    private List<GcAccess> accessList;

    @TableField(value = "channel_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray channelJson;

    public JSONArray getRoleJson() {
        return roleJson;
    }

    public void setRoleJson(JSONArray roleJson) {
        this.roleJson = roleJson;
    }

    @TableField(value = "role_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray roleJson;

    @TableField(value = "subscribe_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray subscribeJson;

    @ApiModelProperty(value = "门户编辑code，0-覆盖所有使用该code的权限，1-不覆盖，在用户当前权限上修改，2-不修改用户权限")
    @TableField(exist = false)
    private Integer saveType;

    @TableField(exist = false)
    private List<GcUser> users;

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    @TableField(exist = false)
    private String parentCode;

    private String groupName;

    private String tryForFreeLink;

    private String tryForFreeText;

    public Integer getSubjectNum() {
        return subjectNum;
    }

    public void setSubjectNum(Integer subjectNum) {
        this.subjectNum = subjectNum;
    }

    public Integer getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(Integer channelNum) {
        this.channelNum = channelNum;
    }

    public GcUser getUser() {
        return user;
    }

    public void setUser(GcUser user) {
        this.user = user;
    }

    @TableField(exist = false)
    private Integer subjectNum;

    @TableField(exist = false)
    private Integer channelNum;

    @TableField(exist = false)
    private GcUser user;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTryForFreeLink() {
        return tryForFreeLink;
    }

    public void setTryForFreeLink(String tryForFreeLink) {
        this.tryForFreeLink = tryForFreeLink;
    }

    public String getTryForFreeText() {
        return tryForFreeText;
    }

    public void setTryForFreeText(String tryForFreeText) {
        this.tryForFreeText = tryForFreeText;
    }

    private Integer packageBadgeFlag;

    private String packageBadgeContent;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPackageLearningHours() {
        return packageLearningHours;
    }

    public void setPackageLearningHours(Double packageLearningHours) {
        this.packageLearningHours = packageLearningHours;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public JSONArray getChannelJson() {
        return channelJson;
    }

    public void setChannelJson(JSONArray channelJson) {
        this.channelJson = channelJson;
    }

    public JSONArray getSubscribeJson() {
        return subscribeJson;
    }

    public void setSubscribeJson(JSONArray subscribeJson) {
        this.subscribeJson = subscribeJson;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getDefaultCodeFlag() {
        return defaultCodeFlag;
    }

    public void setDefaultCodeFlag(Integer defaultCodeFlag) {
        this.defaultCodeFlag = defaultCodeFlag;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getPackageVideoFullUrl() {
        return packageVideoFullUrl;
    }

    public String getPackageSnapShotUrl() {
        return packageSnapShotUrl;
    }

    public void setPackageVideoFullUrl(String packageVideoFullUrl) {
        this.packageVideoFullUrl = packageVideoFullUrl;
    }

    public Integer getPackageBadgeFlag() {
        return packageBadgeFlag;
    }

    public void setPackageBadgeFlag(Integer packageBadgeFlag) {
        this.packageBadgeFlag = packageBadgeFlag;
    }

    public String getPackageBadgeContent() {
        return packageBadgeContent;
    }

    public void setPackageBadgeContent(String packageBadgeContent) {
        this.packageBadgeContent = packageBadgeContent;
    }

    public String getCode() {
        return code;
    }

    public void setPackageSnapShotUrl(String packageSnapShotUrl) {
        this.packageSnapShotUrl = packageSnapShotUrl;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    public Integer getCodeType() {
        return codeType;
    }

    public SysFile getPackageVideoFile() {
        return packageVideoFile;
    }

    public void setPackageVideoFile(SysFile packageVideoFile) {
        this.packageVideoFile = packageVideoFile;
    }

    public void setCodeType(Integer codeType) {
        this.codeType = codeType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPackageVideoFileId() {
        return packageVideoFileId;
    }

    public void setPackageVideoFileId(String packageVideoFileId) {
        this.packageVideoFileId = packageVideoFileId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public JSONArray getPackagePricePeriod() {
        return packagePricePeriod;
    }

    public void setPackagePricePeriod(JSONArray packagePricePeriod) {
        this.packagePricePeriod = packagePricePeriod;
    }

    public Integer getFreeFlag() {
        return freeFlag;
    }

    public void setFreeFlag(Integer freeFlag) {
        this.freeFlag = freeFlag;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getOwnedFlag() {
        return ownedFlag;
    }

    public void setOwnedFlag(Integer ownedFlag) {
        this.ownedFlag = ownedFlag;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public String getPackageAdditionalCourseInformation() {
        return packageAdditionalCourseInformation;
    }

    public void setPackageAdditionalCourseInformation(String packageAdditionalCourseInformation) {
        this.packageAdditionalCourseInformation = packageAdditionalCourseInformation;
    }

    public Integer getPackageImgId() {
        return packageImgId;
    }

    public void setPackageImgId(Integer packageImgId) {
        this.packageImgId = packageImgId;
    }

    public Integer getPackageShowFlag() {
        return packageShowFlag;
    }

    public void setPackageShowFlag(Integer packageShowFlag) {
        this.packageShowFlag = packageShowFlag;
    }

    public SysFile getPackageImgFile() {
        return packageImgFile;
    }

    public void setPackageImgFile(SysFile packageImgFile) {
        this.packageImgFile = packageImgFile;
    }

    public String getPackageImgFullUrl() {
        return packageImgFullUrl;
    }

    public void setPackageImgFullUrl(String packageImgFullUrl) {
        this.packageImgFullUrl = packageImgFullUrl;
    }

    public Long getPackageCourseTotalTime() {
        return packageCourseTotalTime;
    }

    public void setPackageCourseTotalTime(Long packageCourseTotalTime) {
        this.packageCourseTotalTime = packageCourseTotalTime;
    }

    public Double getPackageCourseAvgStars() {
        return packageCourseAvgStars;
    }

    public void setPackageCourseAvgStars(Double packageCourseAvgStars) {
        this.packageCourseAvgStars = packageCourseAvgStars;
    }

    public Long getPackageCourseStarUsers() {
        return packageCourseStarUsers;
    }

    public void setPackageCourseStarUsers(Long packageCourseStarUsers) {
        this.packageCourseStarUsers = packageCourseStarUsers;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public List<GcSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<GcSubject> subjects) {
        this.subjects = subjects;
    }

    public Date getPackageExpiredTime() {
        return packageExpiredTime;
    }

    public void setPackageExpiredTime(Date packageExpiredTime) {
        this.packageExpiredTime = packageExpiredTime;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    @Override
    public String toString() {
        return "GcAccess{"
                + "id="
                + id
                + ", masterId="
                + masterId
                + ", roleType="
                + roleType
                + ", code='"
                + code
                + '\''
                + ", adminId="
                + adminId
                + ", codeType="
                + codeType
                + ", userNum="
                + userNum
                + ", updateTime="
                + updateTime
                + ", createTime="
                + createTime
                + ", freeFlag="
                + freeFlag
                + ", packageName='"
                + packageName
                + '\''
                + ", packagePricePeriod="
                + packagePricePeriod
                + ", packageDescription='"
                + packageDescription
                + '\''
                + ", packageAdditionalCourseInformation='"
                + packageAdditionalCourseInformation
                + '\''
                + ", packageImgId="
                + packageImgId
                + ", packageShowFlag="
                + packageShowFlag
                + ", ownedFlag="
                + ownedFlag
                + ", packageImgFile="
                + packageImgFile
                + ", packageImgFullUrl='"
                + packageImgFullUrl
                + '\''
                + ", packageCourseTotalTime="
                + packageCourseTotalTime
                + ", packageCourseAvgStars="
                + packageCourseAvgStars
                + ", times="
                + times
                + ", packageCourseStarUsers="
                + packageCourseStarUsers
                + ", subjects="
                + subjects
                + ", defaultCodeFlag="
                + defaultCodeFlag
                + ", packageLearningHours="
                + packageLearningHours
                + ", fileType='"
                + fileType
                + '\''
                + ", packageVideoFileId='"
                + packageVideoFileId
                + '\''
                + ", packageVideoFile="
                + packageVideoFile
                + ", packageSnapShotUrl='"
                + packageSnapShotUrl
                + '\''
                + ", packageVideoFullUrl='"
                + packageVideoFullUrl
                + '\''
                + ", packageExpiredTime="
                + packageExpiredTime
                + ", order="
                + order
                + ", saveType="
                + saveType
                + ", packageBadgeFlag="
                + packageBadgeFlag
                + ", packageBadgeContent='"
                + packageBadgeContent
                + '\''
                + '}';
    }

    public List<GcUser> getUsers() {
        return users;
    }

    public void setUsers(List<GcUser> users) {
        this.users = users;
    }
}
