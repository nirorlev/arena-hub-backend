package com.threeatom.guidecore.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "course_settings", autoResultMap = true)
public class CourseSetting {
    private Integer id;
    private Integer courseId;

    private Integer courseCompletionPercentage;
    private Integer contentCompletionPercentage;
}
