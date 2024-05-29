package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "GcUserSaveContent", description = "用户保存的内容")
public class GcUserSaveContent implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "用户Id")
    private Integer userId;
    @ApiModelProperty(value = "门户Id")
    private Integer masterId;
    @ApiModelProperty(value = "保存文件夹Id")
    private Integer folderId;
    @ApiModelProperty(value = "文件id")
    private Integer fileId;
    @ApiModelProperty(value = "视频Id")
    private Integer videoId;
    @TableField(exist = false)
    @JSONField(deserialize = false)
    @ApiModelProperty(value = "视频对象")
    private GcVideo video;
    @ApiModelProperty(value = "课程Id")
    private Integer subId;
    @TableField(exist = false)
    @JSONField(deserialize = false)
    @ApiModelProperty(value = "课程对象")
    private GcSubject subject;
    private Date updateTime;
    @TableField(exist = false)
    private SysFile videoFile;
    private Date createTime;
    private Integer contentId;

    public GcUserSaveContent(Integer userId, Integer masterId, Integer folderId) {
        this.userId = userId;
        this.masterId = masterId;
        this.folderId = folderId;
    }
}
