package com.threeatom.guidecore.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "courses_progress", autoResultMap = true)
public class CourseProgress {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long enrollmentId;

    private Double percentage;

    private Integer secondsViewed;
    private Integer completedSectionsCount;
    private Integer completedTasksCount;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
