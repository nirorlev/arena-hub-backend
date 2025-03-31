package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "course_enrollments", autoResultMap = true)
public class CourseEnrollment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer userId;
    private Integer courseId;

    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private OffsetDateTime complianceDate;

    private OffsetDateTime updateTime = OffsetDateTime.now();
}