package com.threeatom.guidecore.controller.user.vo;

import com.alibaba.fastjson.JSONArray;
import java.util.Date;
import lombok.Data;

@Data
public class ScheduleParamVo {

    private String scheduleName; // 计划名
    private JSONArray userIds; // 用户id，list
    private Date startDate; // 计划开始时间
    private Date endDate; // 计划结束时间
    private Integer subId; // 课程id，level=1或2时需要
    private Integer videoId; // 视频id，level=11时需要
    private JSONArray weekDates; // 1-6=周一至周六，0=周日
    private Integer level; // 级别，1=1级课程，2=2级话题，11=视频
}
