package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @title: videoLongVo
 * @projectName guidecore
 * @description: TODO
 * @date 2021/12/16/01612:11
 */
@Data
public class videoLongVo {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        //视频时长
        private Integer videoLong;

        public Integer getVideoCount() {
                return videoCount;
        }

        public void setVideoCount(Integer videoCount) {
                this.videoCount = videoCount;
        }

        //fid
        private Integer subId;
        //视频数量
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
                return "videoLongVo{" +
                        "videoLong=" + videoLong +
                        ", subId=" + subId +
                        '}';
        }
}
