package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

@Data
public class videoLongVo {
    private static final long serialVersionUID = 1L;

    private Integer videoLong;

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

    private Integer subId;
    private Integer videoCount;

    public Integer getVideoLong() {
        return videoLong;
    }

    public void setVideoLong(Integer videoLong) {
        this.videoLong = videoLong;
    }

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    @Override
    public String toString() {
        return "videoLongVo{" + "videoLong=" + videoLong + ", subId=" + subId + '}';
    }
}
