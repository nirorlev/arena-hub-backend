package com.threeatom.guidecore.entity;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @auther: rjunchao
 * @date: 2021/8/24 19:41
 * @desc: 课程总计信息对象
 */
@Data
public class SubjectTotals {

    private Integer totalProgressPercent;//进度百分比

    private List<GcSubject> subjects;//播放进度

    private Integer completeProgress;//完成进度
    private Integer totalProgress;//总进度

    private Long videoCompleteProgress;//视频看的分钟数
    private Long videoTotalProgress;//视频总分钟数


    private Integer lessonsCompleteProgress;//视频完成数
    private Integer lessonsTotalProgress;//视频总数


    private Integer taskCompleteProgress;//视频问题回答数
    private Integer taskTotalProgress;//视频问题总数

    private String completionDate;
    private String startTime;

    private Integer userId;
}
