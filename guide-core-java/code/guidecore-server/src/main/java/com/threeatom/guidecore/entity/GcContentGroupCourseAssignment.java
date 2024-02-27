package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName(value = "gc_content_group_course_assignment", autoResultMap = true)
public class GcContentGroupCourseAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    private GcSubject course;
    @TableField(exist = false)
    private GcUser user;
    @TableField(exist = false)
    private GcAccess access;

    @TableField(value = "is_mandatory")
    private Boolean mandatory = false;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime modifiedDate = LocalDateTime.now();
}
