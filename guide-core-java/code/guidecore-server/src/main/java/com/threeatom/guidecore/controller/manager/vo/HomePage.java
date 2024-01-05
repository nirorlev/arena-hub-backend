package com.threeatom.guidecore.controller.manager.vo;

import com.threeatom.guidecore.entity.GcMaster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "首页仪表盘VO")
public class HomePage {

    @ApiModelProperty(value = "活动用户")
    private Integer activeUserNum;
    @ApiModelProperty(value = "空间信息")
    private GcMaster master;
    @ApiModelProperty(value = "主题数")
    private Integer subNum;
    @ApiModelProperty(value = "专题数量")
    private Integer topicNum;
    @ApiModelProperty(value = "视频数量")
    private Integer videoNum;
    @ApiModelProperty(value = "资源数量")
    private Integer resNum;
    @ApiModelProperty(value = "空间管理员用户")
    private Integer adminCodeUserNum;
    @ApiModelProperty(value = "空间普通用户")
    private Integer userCodeUserNum;
    @ApiModelProperty(value = "课程管理员用户")
    private Integer subjectAdminCodeUserNum;
    @ApiModelProperty(value = "空间管理员code")
    private Integer adminCodeNum;
    @ApiModelProperty(value = "空间普通code")
    private Integer userCodeNum;

    public Integer getAdminCodeNum() {
        return adminCodeNum;
    }

    public Integer getSubjectAdminCodeUserNum() {
        return subjectAdminCodeUserNum;
    }

    public void setSubjectAdminCodeUserNum(Integer subjectAdminCodeUserNum) {
        this.subjectAdminCodeUserNum = subjectAdminCodeUserNum;
    }

    public void setAdminCodeNum(Integer adminCodeNum) {
        this.adminCodeNum = adminCodeNum;
    }

    public Integer getUserCodeNum() {
        return userCodeNum;
    }

    public void setUserCodeNum(Integer userCodeNum) {
        this.userCodeNum = userCodeNum;
    }

    public Integer getActiveUserNum() {
        return activeUserNum;
    }

    public void setActiveUserNum(Integer activeUserNum) {
        this.activeUserNum = activeUserNum;
    }

    public GcMaster getMaster() {
        return master;
    }

    public void setMaster(GcMaster master) {
        this.master = master;
    }

    public Integer getSubNum() {
        return subNum;
    }

    public void setSubNum(Integer subNum) {
        this.subNum = subNum;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(Integer videoNum) {
        this.videoNum = videoNum;
    }

    public Integer getResNum() {
        return resNum;
    }

    public void setResNum(Integer resNum) {
        this.resNum = resNum;
    }

    public Integer getAdminCodeUserNum() {
        return adminCodeUserNum;
    }

    public void setAdminCodeUserNum(Integer adminCodeUserNum) {
        this.adminCodeUserNum = adminCodeUserNum;
    }

    public Integer getUserCodeUserNum() {
        return userCodeUserNum;
    }

    public void setUserCodeUserNum(Integer userCodeUserNum) {
        this.userCodeUserNum = userCodeUserNum;
    }


}
