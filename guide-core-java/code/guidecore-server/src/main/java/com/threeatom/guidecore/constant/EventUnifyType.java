package com.threeatom.guidecore.constant;

import java.util.ArrayList;
import java.util.List;

public class EventUnifyType {

    public static final int TEXT_0 = 0; // 文字
    public static final int IMAGE_1 = 1; // 照片
    public static final int VIDEO_2 = 2; // 视频
    public static final int AUDIO_3 = 3; // 音频
    public static final int DOC_4 = 4; // 文档
    public static final int QUESTION_5 = 5; // 问题回答消息

    public static final int SCREENROCK_6 = 6; // ScreenRock视频链接
    public static final int TENCENT_VIDEO = 7; // 腾讯视频链接
    public static final int RES_FILE_LINK = 8; // 视频添加资源的文件的链接，如：google doc文档
    public static final int RES_FILE = 9; // 视频添加资源的文件，含word，PDF，MP3等
    public static final int SRT_12 = 12; // 字幕文件
    public static final int YOUTUBE_13 = 13;
    public static final int VIMEO_FILE_TYPE_INDEX = 14;
    public static final int WISTIA_FILE_TYPE_INDEX = 15;
    public static final int POWTOON_KALTURA_FILE_TYPE_INDEX = 16;
    public static final int GOOGLE_DRIVE_FILE_TYPE_INDEX = 18;
    public static final int POWTOON_MUX_FILE_TYPE_INDEX = 20;

    public static final String JSON_STR012346 = "[0,1,2,3,4,6]";
    public static final String JSON_STR012345 = "[0,1,2,3,4,5]";
    public static final String NOT_QUESTION_012346 = "0,1,2,3,4,6";

    public static final List<Integer> powtoonVideoFileTypes = List.of(
        POWTOON_KALTURA_FILE_TYPE_INDEX, POWTOON_MUX_FILE_TYPE_INDEX);

    public static final List<Integer> VIDEO_TYPES =
        new ArrayList<Integer>() {
            {
                this.add(VIDEO_2);
                this.add(SCREENROCK_6);
                this.add(TENCENT_VIDEO);
                this.add(YOUTUBE_13);
                this.add(VIMEO_FILE_TYPE_INDEX);
                this.add(WISTIA_FILE_TYPE_INDEX);
                this.add(POWTOON_KALTURA_FILE_TYPE_INDEX);
                this.add(POWTOON_MUX_FILE_TYPE_INDEX);
                this.add(GOOGLE_DRIVE_FILE_TYPE_INDEX);
            }
        };

    public static final List<Integer> RES_TYPES =
        new ArrayList<Integer>() {
            {
                this.add(RES_FILE_LINK);
                this.add(RES_FILE);
            }
        };

    public static final List<Integer> POWTOON_RES_TYPES =
        new ArrayList<Integer>() {
            {
                this.add(RES_FILE_LINK);
                this.add(RES_FILE);
                this.add(VIDEO_2);
                this.add(SCREENROCK_6);
                this.add(YOUTUBE_13);
                this.add(VIMEO_FILE_TYPE_INDEX);
                this.add(WISTIA_FILE_TYPE_INDEX);
                this.add(POWTOON_KALTURA_FILE_TYPE_INDEX);
                this.add(POWTOON_MUX_FILE_TYPE_INDEX);
                this.add(GOOGLE_DRIVE_FILE_TYPE_INDEX);
            }
        };
}
