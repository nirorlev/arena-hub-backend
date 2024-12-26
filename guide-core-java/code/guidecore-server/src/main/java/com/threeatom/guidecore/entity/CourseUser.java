package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@TableName(autoResultMap = true)
@NoArgsConstructor
public class CourseUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer userId;
    private Integer courseId;
    private OffsetDateTime enrollmentDate;
    private OffsetDateTime completionDate;
    private OffsetDateTime createTime = OffsetDateTime.now();
    private OffsetDateTime updateTime = OffsetDateTime.now();
}