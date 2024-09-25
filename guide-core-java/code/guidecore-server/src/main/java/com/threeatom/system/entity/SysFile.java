package com.threeatom.system.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.guidecore.entity.GcUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.Data;

@ApiModel(value = "SysFile对象", description = "")
@Data
public class SysFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("业务实例id")
    private Integer sysId;

    @ApiModelProperty("上传者Id")
    private Integer uploadUid;

    @ApiModelProperty("上传者角色")
    private Integer userRole;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("文件夹目录")
    private String folder;

    @ApiModelProperty("文件URL")
    private String fileUrl;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("文件类型枚举index")
    private Integer fileTypeIndex;

    @ApiModelProperty("文件备注")
    @TableField(value = "file_remark", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray fileRemark = new JSONArray();

    @ApiModelProperty("文件完整File链接")
    @TableField(exist = false)
    private String fullFileUrl;

    @TableField(exist = false)
    private String snapshotUrl;

    @TableField(exist = false)
    private Integer likedFlag;

    @ApiModelProperty("文件储存方式")
    private Integer saveType;

    @ApiModelProperty("文件的md5值")
    private String md5;

    @TableField(exist = false)
    @ApiModelProperty("字幕文件url")
    private String captionUrl;

    @ApiModelProperty("文件大小")
    private String size;

    @ApiModelProperty("视频时长，视频时适用")
    private Integer videoLong;

    private Date updateTime;
    private Date createTime;

    private String description;

    @ApiModelProperty("gc_master的id")
    private Integer masterId;

    @ApiModelProperty("是否开启字幕")
    private String ifCaption;

    @ApiModelProperty("字幕语言")
    @TableField(value = "target_lang_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray targetLangJson = new JSONArray();

    @TableField(exist = false)
    private List<String> youtubeUrl;

    @TableField(exist = false)
    private List<Integer> youtubeTime;

    @TableField(exist = false)
    private List<String> youtubeName;

    @TableField(exist = false)
    private Integer likeNum;

    private String thumbNailUrl;

    @TableField(exist = false)
    private List<String> tags;

    @TableField(exist = false)
    private Integer ptChannelContentId;

    @TableField(exist = false)
    private GcUser gcUser;

    @TableField(exist = false)
    private Integer videoId;

    @TableField(exist = false)
    private Integer isLiked;

    @TableField(exist=false)
    private JSONObject source;
    private Integer thumbNailId;

    private String uuid;

    @TableField(exist = false)
    private Integer contentId;

    @TableField(exist = false)
    private Boolean isPrivate = false;

    @JSONField(serialize = false)
    public String getIdToString() {
        return this.id.toString();
    }

    @TableField(exist = false)
    public List<String> courseTags;

    @TableField(exist = false)
    public String describe;

    @TableField(exist = false)
    public String originFileName;

    @TableField(exist = false)
    public Map<String, Boolean> permissions;
}
