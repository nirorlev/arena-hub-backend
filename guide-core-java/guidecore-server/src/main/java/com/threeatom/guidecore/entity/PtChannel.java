package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pagehelper.PageInfo;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.guidecore.enums.ChannelVisibilityFlag;
import com.threeatom.system.entity.SysFile;
import com.threeatom.utils.data.TreeNodeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiModel(value = "PtChannel", description = "")
@TableName(autoResultMap = true)
public class PtChannel implements Serializable, TreeNodeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @EqualsAndHashCode.Include
    private Integer id;

    @EqualsAndHashCode.Include
    private String channelName;

    @EqualsAndHashCode.Include
    private Integer channelImgFileId;

    @TableField(value = "\"desc\"")
    @EqualsAndHashCode.Include
    private String desc;

    @EqualsAndHashCode.Include
    private Integer createUserId;

    @EqualsAndHashCode.Include
    private Integer visibleFlag;

    @TableField(exist = false)
    private Boolean isPrivate;

    private Integer categoryId;

    @TableField(value = "\"level\"")
    @EqualsAndHashCode.Include
    private Integer level;

    @EqualsAndHashCode.Include
    private Integer fid;

    @TableField(value = "\"order\"")
    private Integer order;

    private Integer masterId;

    @EqualsAndHashCode.Include
    private String channelSlug;

    private Integer channelAvatarFileId;

    @TableField(exist = false)
    private OffsetDateTime subscriptionTime;

    @TableField(exist = false)
    private SysFile avatarFile;

    @TableField(exist = false)
    private String avatarFullFileUrl;

    // 订阅用户list
    @TableField(exist = false)
    private List<GcUser> userList;

    @TableField(exist = false)
    private String imgFullFileUrl;

    @ApiModelProperty(value = "所有tag")
    @TableField(exist = false)
    private List<String> allTags;

    @TableField(exist = false)
    private List<SysFile> videoList;

    private Boolean isDeleted;

    private OffsetDateTime updateTime;

    private OffsetDateTime createTime;

    @ApiModelProperty("课程tag标签")
    @TableField(value = "tags", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray tags = new JSONArray();

    @TableField(exist = false)
    private List<PtChannel> sectionList;

    @TableField(exist = false)
    private Integer followFlag;

    @TableField(exist = false)
    private String createUserFirstName;

    @TableField(exist = false)
    private String createUserLastName;

    @TableField(exist = false)
    private Integer ownFlag;

    @TableField(exist = false)
    private PageInfo sectionPageInfo;

    @TableField(exist = false)
    private GcUser createUser;

    @TableField(exist = false)
    private Integer subscribeNum;

    @TableField(exist = false)
    private String channelTags;

    @TableField(exist = false)
    private String searchName;

    @TableField(exist = false)
    private List<Integer> accessIdList;

    @TableField(exist = false)
    private List<Integer> permissionAccessIdList;

    @TableField(exist = false)
    private List<Integer> subscribeAccessIdList;

    @TableField(
            value = "subscribeAccessIds",
            typeHandler = FastJsonArrayTypeHandler.class,
            exist = false)
    private JSONArray subscribeAccessIds;

    @TableField(exist = false)
    private String subscribeUserIds = new String();

    @TableField(value = "accessIds", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray accessIds;

    /**
     * 是否订阅全部 0是 1否
     */
    @TableField(exist = false)
    public Integer isAllSubscribe;

    /**
     * 是否发布全部 0是 1否
     */
    @TableField(exist = false)
    public Integer isAllChoose;

    @TableField(exist = false)
    public Integer fileId;

    @TableField(exist = false)
    public SysFile videoFile;

    @TableField(exist = false)
    private Integer likeNum;

    @TableField(exist = false)
    private Integer channelContentId;

    /**
     * 点过1 没点过0
     */
    @TableField(exist = false)
    private Integer isLiked;

    @TableField(exist = false)
    private List<GcAccess> subscribeAccessList;

    @TableField(exist = false)
    private List<GcAccess> accessList;

    @TableField(exist = false)
    private Map<String, Boolean> permissions;

    @TableField(exist = false)
    private Integer contentCount;

    @TableField(exist = false)
    private OffsetDateTime lastContentUpdatedTime;

    public Boolean getIsPrivate() {
        return ChannelVisibilityFlag.PRIVATE.getValue().equals(visibleFlag);
    }

    public boolean isPrivate() {
        return getIsPrivate();
    }

    public boolean isPublic() {
        return ChannelVisibilityFlag.PUBLIC.getValue().equals(visibleFlag);
    }

    public boolean isCertainTeams() {
        return ChannelVisibilityFlag.CERTAIN_TEAMS.getValue().equals(visibleFlag);
    }

    public boolean isSection() {
        return fid != null;
    }
}
