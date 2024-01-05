package com.threeatom.system.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.guidecore.entity.GcVideoComment;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @title: SysFileCaption
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/8/00810:05
 */
public class SysFileCaption implements Serializable {
    @ApiModelProperty("主键")
    @TableId(
            value = "id",
            type = IdType.AUTO
    )
    private Integer id;
    @ApiModelProperty("云猫任务ID")
    private String ymTaskId;
    @ApiModelProperty("云猫code")
    private Integer ymCode;
    @ApiModelProperty("srt数据")
    private String ymSrtData;
    @ApiModelProperty("云猫消息")
    private String ymMessage;
    @ApiModelProperty("视频文件ID")
    private Integer videoId;
    @ApiModelProperty("字幕文件ID")
    private Integer captionFileId;
    @ApiModelProperty("创建时间")
    private DateTime create_time;
    @ApiModelProperty("更新时间")
    private DateTime update_time;
    @ApiModelProperty("原语言")
    private String lang;
    @ApiModelProperty("状态 0关1开")
    private Integer state;
    @TableField(exist = false)
    @ApiModelProperty("字幕文件url")
    private String captionUrl;
    @TableField(exist = false)
    @ApiModelProperty(value = "字幕文件列表")
    private SysFile sysFileList;
    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    @ApiModelProperty(value = "新添加的字幕语言")
    private List<String> newTargetLangJson;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getNewTargetLangJson() {
        return newTargetLangJson;
    }

    public void setNewTargetLangJson(List<String> newTargetLangJson) {
        this.newTargetLangJson = newTargetLangJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYmCode() {
        return ymCode;
    }

    public void setYmCode(Integer ymCode) {
        this.ymCode = ymCode;
    }

    public String getYmSrtData() {
        return ymSrtData;
    }

    public void setVideoId(Integer videoId) {
        this.videoId = videoId;
    }

    public void setYmTaskId(String ymTaskId) {
        this.ymTaskId = ymTaskId;
    }

    public Integer getVideoId() {
        return videoId;
    }

    public Integer getCaptionFileId() {
        return captionFileId;
    }

    public void setCaptionFileId(Integer captionFileId) {
        this.captionFileId = captionFileId;
    }

    public void setYmSrtData(String ymSrtData) {
        this.ymSrtData = ymSrtData;
    }

    public String getYmMessage() {
        return ymMessage;
    }

    public void setYmMessage(String ymMessage) {
        this.ymMessage = ymMessage;
    }

    public DateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(DateTime create_time) {
        this.create_time = create_time;
    }

    public DateTime getUpdate_time() {
        return update_time;
    }

    public String getYmTaskId() {
        return ymTaskId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public String getCaptionUrl() {
        return captionUrl;
    }

    public void setCaptionUrl(String captionUrl) {
        this.captionUrl = captionUrl;
    }

    public SysFile getSysFileList() {
        return sysFileList;
    }

    public void setSysFileList(SysFile sysFileList) {
        this.sysFileList = sysFileList;
    }

    public void setUpdate_time(DateTime update_time) {
        this.update_time = update_time;
    }


    @Override
    public String toString() {
        return "SysFileCaption{" +
                "id=" + id +
                ", ymTaskId='" + ymTaskId + '\'' +
                ", ymCode=" + ymCode +
                ", ymSrtData='" + ymSrtData + '\'' +
                ", ymMessage='" + ymMessage + '\'' +
                ", videoId=" + videoId +
                ", captionFileId=" + captionFileId +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", lang='" + lang + '\'' +
                ", captionUrl='" + captionUrl + '\'' +
                ", sysFileList=" + sysFileList +
                '}';
    }
}
