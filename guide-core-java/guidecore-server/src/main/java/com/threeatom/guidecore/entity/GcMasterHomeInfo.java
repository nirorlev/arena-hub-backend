package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
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

@ApiModel(value = "GcMasterHomeInfo", description = "门户首页信息")
@TableName(autoResultMap = true)
public class GcMasterHomeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "门户Id")
    private Integer masterId;

    @ApiModelProperty(value = "内容名称，")
    private String name;

    @ApiModelProperty(value = "各组内部排序")
    private Integer order;

    @ApiModelProperty(value = "1或null=文字，2=图片，3=视频")
    private Integer type;

    @ApiModelProperty(value = "文字内容")
    private String content;

    @ApiModelProperty(value = "type=2或3时的文件id")
    private Integer fileId;

    @ApiModelProperty(value = "fileId的文件")
    @TableField(exist = false)
    private SysFile file;

    @ApiModelProperty(value = "逻辑删除,-1=关闭")
    private Integer state;

    @ApiModelProperty(value = "")
    private Date updateTime;

    @ApiModelProperty(value = "")
    private Date createTime;

    @ApiModelProperty("channelids")
    @TableField(value = "channel_ids", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray channelIds = new JSONArray();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public SysFile getFile() {
        return file;
    }

    public void setFile(SysFile file) {
        this.file = file;
    }

    public JSONArray getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(JSONArray channelIds) {
        this.channelIds = channelIds;
    }
}
