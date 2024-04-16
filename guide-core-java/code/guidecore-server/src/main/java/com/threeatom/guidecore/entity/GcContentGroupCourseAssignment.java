package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@TableName(value = "gc_content_group_course_assignment", autoResultMap = true)
public class GcContentGroupCourseAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer contentGroupId;
    private Integer courseId;
    private Integer createdByUserId;

    @TableField(exist = false)
    private GcSubject course;
    @TableField(exist = false)
    private GcUser createdBy;
    @TableField(exist = false)
    private GcAccess contentGroup;

    @TableField(value = "is_mandatory")
    private Boolean mandatory = false;
    private OffsetDateTime deadline;

    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime modifiedDate = OffsetDateTime.now();
}
