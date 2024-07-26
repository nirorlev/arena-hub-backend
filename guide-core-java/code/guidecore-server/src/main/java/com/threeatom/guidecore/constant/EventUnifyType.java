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
    public static final int SCHEDULE10 = 10; // 视频添加资源的文件，含word，PDF，MP3等
    public static final int event_link = 11; // 视频事件关联链接
    public static final int SRT_12 = 12; // 字幕文件
    public static final int YOUTUBE_13 = 13;
    public static final int vimeoFileTypeIndex = 14;
    public static final int wistiaFileTypeIndex = 15;
    public static final int powtoonFileTypeIndex = 16;
    public static final int googleDriveFileTypeIndex = 18;
    public static final String video_links_JSON_STR = "[6,7,8,11,13,14,15,16]";

    public static final String JSON_STR012346 = "[0,1,2,3,4,6]";
    public static final String JSON_STR012345 = "[0,1,2,3,4,5]";
    public static final String notQuestion012346 = "0,1,2,3,4,6";

    public static final List<Integer> screenRocks =
            new ArrayList<Integer>() {
                {
                    this.add(SCREENROCK_6);
                }
            };

    public static final List<Integer> videoTypes =
            new ArrayList<Integer>() {
                {
                    this.add(VIDEO_2);
                    this.add(SCREENROCK_6);
                    this.add(TENCENT_VIDEO);
                    this.add(YOUTUBE_13);
                    this.add(vimeoFileTypeIndex);
                    this.add(wistiaFileTypeIndex);
                    this.add(powtoonFileTypeIndex);
                    this.add(googleDriveFileTypeIndex);
                }
            };

    public static final List<Integer> resTypes =
            new ArrayList<Integer>() {
                {
                    this.add(RES_FILE_LINK);
                    this.add(RES_FILE);
                }
            };

    public static final List<Integer> powtoonResTypes =
            new ArrayList<Integer>() {
                {
                    this.add(RES_FILE_LINK);
                    this.add(RES_FILE);
                    this.add(VIDEO_2);
                    this.add(SCREENROCK_6);
                    this.add(YOUTUBE_13);
                    this.add(vimeoFileTypeIndex);
                    this.add(wistiaFileTypeIndex);
                    this.add(powtoonFileTypeIndex);
                    this.add(googleDriveFileTypeIndex);
                }
            };
}
