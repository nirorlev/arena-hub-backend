package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private OffsetDateTime endDate;

    private OffsetDateTime complianceDate;
    private OffsetDateTime completionDate;

    private OffsetDateTime updatedTime = OffsetDateTime.now();

    @TableField(exist = false)
    private List<CourseProgress> courseProgress = new ArrayList<>();

    @TableField(exist = false)
    private GcUser user;

    @TableField(exist = false)
    private GcSubject course;

    public Optional<CourseProgress> getLatestProgress() {
        return courseProgress.stream()
            .min(Comparator.comparing(CourseProgress::getCreatedTime));
    }
}