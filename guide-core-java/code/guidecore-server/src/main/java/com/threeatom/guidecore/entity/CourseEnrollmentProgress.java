package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "createdTime", "updatedTime"})
@TableName(value = "course_enrollments_progress", autoResultMap = true)
public class CourseEnrollmentProgress {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long enrollmentId;

    private double percentage;

    private Integer secondsViewed;
    private Integer completedSectionsCount;
    private Integer completedTasksCount;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
