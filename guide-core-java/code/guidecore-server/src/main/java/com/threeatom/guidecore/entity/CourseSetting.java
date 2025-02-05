package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "course_settings", autoResultMap = true)
public class CourseSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer courseId;

    private Integer courseContentStudyPercentage;
    private Integer singleVideoViewPercentage;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
